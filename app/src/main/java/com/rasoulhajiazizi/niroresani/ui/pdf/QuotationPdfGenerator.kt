package com.rasoulhajiazizi.niroresani.ui.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

/**
 * تولید فایل PDF پیش‌فاکتور - فاز ۶.
 *
 * تصمیم فنی مهم: به‌جای iText7، از موتور بومی اندروید (android.graphics.pdf.PdfDocument
 * + StaticLayout) استفاده شده است. دلیل: iText7 نسخه رایگان، اتصال صحیح حروف فارسی
 * (Contextual Shaping) را پشتیبانی نمی‌کند و فقط با ماژول تجاری pdfCalligraph درست
 * می‌شود. موتور متن بومی اندروید (مبتنی بر HarfBuzz) همان رندر صحیحی را که در رابط
 * کاربری برنامه تایید شده، در PDF هم تولید می‌کند.
 */
object QuotationPdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 در واحد pt (72dpi)
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    // عرض ستون‌های جدول (چپ به راست در حافظه؛ ترتیب نمایش با RTL خودکار مدیریت می‌شود)
    private const val COL_ROW_NO = 28f
    private const val COL_TOTAL = 95f
    private const val COL_UNIT_PRICE = 95f
    private const val COL_UNIT = 45f
    private const val COL_QTY = 50f
    private val COL_TITLE = CONTENT_WIDTH - COL_ROW_NO - COL_TOTAL - COL_UNIT_PRICE - COL_UNIT - COL_QTY

    data class PdfResult(val file: File)

    fun generate(
        context: Context,
        quotation: QuotationEntity,
        items: List<QuotationItemEntity>,
        companyName: String,
        registrationNumber: String,
        logoPath: String?,
        customerName: String,
        customerAddress: String
    ): PdfResult {
        val regularTypeface = Typeface.createFromAsset(context.assets, "fonts/Vazirmatn-Regular.ttf")
        val boldTypeface = Typeface.createFromAsset(context.assets, "fonts/Vazirmatn-Bold.ttf")

        val titlePaint = newPaint(boldTypeface, 16f)
        val headerPaint = newPaint(boldTypeface, 11f)
        val bodyPaint = newPaint(regularTypeface, 10f)
        val smallPaint = newPaint(regularTypeface, 8.5f)
        val tableHeaderPaint = newPaint(boldTypeface, 9.5f)

        // --- مرحله ۱: محاسبه ارتفاع هر ردیف جدول (برای تعیین دقیق تعداد صفحات) ---
        val rowHeights = items.map { item ->
            val layout = buildLayout(item.titleSnapshot, bodyPaint, COL_TITLE - 8f)
            maxOf(22f, layout.height.toFloat() + 8f)
        }

        val firstPageHeaderHeight = 210f // فضای سربرگ کامل (لوگو+شرکت+مشتری+سند) فقط صفحه اول
        val repeatHeaderHeight = 40f // سربرگ کوتاه صفحات بعدی
        val tableHeaderHeight = 24f
        val footerReserve = 90f // فضای جمع کل + توضیحات + امضا (فقط صفحه آخر)
        val pageNumberReserve = 20f

        val totalPages = calculateTotalPages(
            rowHeights, firstPageHeaderHeight, repeatHeaderHeight,
            tableHeaderHeight, footerReserve, pageNumberReserve
        )

        // --- مرحله ۲: رندر واقعی PDF ---
        val pdfDocument = PdfDocument()
        var pageIndex = 1
        var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex).create())
        var canvas = page.canvas
        var y = MARGIN

        y = drawFullHeader(
            canvas, context, y, quotation, companyName, registrationNumber, logoPath,
            customerName, customerAddress, titlePaint, headerPaint, bodyPaint
        )
        y = drawTableHeader(canvas, y, tableHeaderPaint)

        val bottomLimit = PAGE_HEIGHT - MARGIN - pageNumberReserve

        items.forEachIndexed { index, item ->
            val rowHeight = rowHeights[index]
            if (y + rowHeight > bottomLimit) {
                drawPageNumber(canvas, pageIndex, totalPages, smallPaint)
                pdfDocument.finishPage(page)
                pageIndex++
                page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex).create())
                canvas = page.canvas
                y = MARGIN
                y = drawRepeatHeader(canvas, y, quotation, headerPaint)
                y = drawTableHeader(canvas, y, tableHeaderPaint)
            }
            y = drawTableRow(canvas, y, rowHeight, index + 1, item, bodyPaint)
        }

        // جمع کل، توضیحات و امضا - اگر جا نبود، صفحه جدید
        if (y + footerReserve > bottomLimit) {
            drawPageNumber(canvas, pageIndex, totalPages, smallPaint)
            pdfDocument.finishPage(page)
            pageIndex++
            page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex).create())
            canvas = page.canvas
            y = MARGIN
            y = drawRepeatHeader(canvas, y, quotation, headerPaint)
        }

        y = drawTotalAndFooter(canvas, y, quotation, headerPaint, bodyPaint, smallPaint)
        drawPageNumber(canvas, pageIndex, totalPages, smallPaint)
        pdfDocument.finishPage(page)

        val pdfDir = File(context.filesDir, "pdf").apply { mkdirs() }
        val fileName = "پیش‌فاکتور_${quotation.number.replace("/", "-")}.pdf"
        val outFile = File(pdfDir, fileName)
        FileOutputStream(outFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        return PdfResult(outFile)
    }

    private fun newPaint(typeface: Typeface, size: Float): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = size
            color = android.graphics.Color.BLACK
        }
    }

    private fun buildLayout(text: String, paint: TextPaint, width: Float): StaticLayout {
        val w = maxOf(width.toInt(), 10)
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, w)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
    }

    /** رسم یک بلوک متن راست‌چین در مختصات مشخص و بازگرداندن ارتفاع مصرف‌شده */
    private fun drawRtlText(canvas: Canvas, text: String, paint: TextPaint, rightX: Float, topY: Float, maxWidth: Float): Float {
        val layout = buildLayout(text, paint, maxWidth)
        canvas.save()
        canvas.translate(rightX - maxWidth, topY)
        layout.draw(canvas)
        canvas.restore()
        return layout.height.toFloat()
    }

    private fun calculateTotalPages(
        rowHeights: List<Float>,
        firstPageHeaderHeight: Float,
        repeatHeaderHeight: Float,
        tableHeaderHeight: Float,
        footerReserve: Float,
        pageNumberReserve: Float
    ): Int {
        val bottomLimit = PAGE_HEIGHT - MARGIN - pageNumberReserve
        var pages = 1
        var y = MARGIN + firstPageHeaderHeight + tableHeaderHeight
        for (h in rowHeights) {
            if (y + h > bottomLimit) {
                pages++
                y = MARGIN + repeatHeaderHeight + tableHeaderHeight
            }
            y += h
        }
        if (y + footerReserve > bottomLimit) pages++
        return pages
    }

    private fun drawFullHeader(
        canvas: Canvas,
        context: Context,
        startY: Float,
        quotation: QuotationEntity,
        companyName: String,
        registrationNumber: String,
        logoPath: String?,
        customerName: String,
        customerAddress: String,
        titlePaint: TextPaint,
        headerPaint: TextPaint,
        bodyPaint: TextPaint
    ): Float {
        var y = startY

        // لوگو - سمت راست بالا
        if (!logoPath.isNullOrBlank()) {
            try {
                val bitmap = BitmapFactory.decodeFile(logoPath)
                if (bitmap != null) {
                    val logoSize = 60f
                    val scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, logoSize.toInt(), logoSize.toInt(), true)
                    canvas.drawBitmap(scaled, PAGE_WIDTH - MARGIN - logoSize, y, null)
                }
            } catch (e: Exception) {
                // نادیده گرفتن خطای بارگذاری لوگو - سند بدون لوگو هم معتبر است
            }
        }

        // عنوان وسط
        drawCenteredText(canvas, "پیش‌فاکتور", titlePaint, y + 20f)

        // اطلاعات شرکت - سمت چپ
        var leftY = y
        leftY += drawLtrAlignedText(canvas, companyName, headerPaint, MARGIN, leftY, 180f) + 2f
        if (registrationNumber.isNotBlank()) {
            drawLtrAlignedText(canvas, "شماره ثبت: ${PersianNumberFormatter.toPersianDigits(registrationNumber)}", bodyPaint, MARGIN, leftY, 180f)
        }

        y += 70f

        // شماره و تاریخ سند - راست‌چین
        y += drawRtlText(canvas, "شماره پیش‌فاکتور: ${PersianNumberFormatter.toPersianDigits(quotation.number)}", bodyPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH / 2) + 4f
        drawRtlText(canvas, "تاریخ: ${quotation.issueDateShamsi}", bodyPaint, PAGE_WIDTH - MARGIN, y - drawRtlTextHeight(bodyPaint), CONTENT_WIDTH / 2)
        y += 6f

        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, Paint().apply { strokeWidth = 1f; color = android.graphics.Color.GRAY })
        y += 14f

        // اطلاعات مشتری
        y += drawRtlText(canvas, "مشتری: $customerName", headerPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH) + 4f
        if (customerAddress.isNotBlank()) {
            y += drawRtlText(canvas, "آدرس: $customerAddress", bodyPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH) + 4f
        }
        y += 10f

        return y
    }

    private fun drawRepeatHeader(canvas: Canvas, startY: Float, quotation: QuotationEntity, headerPaint: TextPaint): Float {
        var y = startY
        y += drawRtlText(
            canvas,
            "پیش‌فاکتور شماره ${PersianNumberFormatter.toPersianDigits(quotation.number)} (ادامه)",
            headerPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH
        ) + 10f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, Paint().apply { strokeWidth = 1f; color = android.graphics.Color.GRAY })
        return y + 12f
    }

    private fun drawTableHeader(canvas: Canvas, startY: Float, paint: TextPaint): Float {
        val y = startY
        val bg = Paint().apply { color = android.graphics.Color.rgb(230, 232, 235) }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, bg)

        var xRight = PAGE_WIDTH - MARGIN
        drawCellText(canvas, "ردیف", paint, xRight, y + 5f, COL_ROW_NO)
        xRight -= COL_ROW_NO
        drawCellText(canvas, "شرح کالا یا خدمت", paint, xRight, y + 5f, COL_TITLE)
        xRight -= COL_TITLE
        drawCellText(canvas, "تعداد", paint, xRight, y + 5f, COL_QTY)
        xRight -= COL_QTY
        drawCellText(canvas, "واحد", paint, xRight, y + 5f, COL_UNIT)
        xRight -= COL_UNIT
        drawCellText(canvas, "قیمت واحد", paint, xRight, y + 5f, COL_UNIT_PRICE)
        xRight -= COL_UNIT_PRICE
        drawCellText(canvas, "مبلغ", paint, xRight, y + 5f, COL_TOTAL)

        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, Paint().apply {
            style = Paint.Style.STROKE; strokeWidth = 0.7f; color = android.graphics.Color.DKGRAY
        })
        return y + 22f
    }

    private fun drawTableRow(
        canvas: Canvas,
        startY: Float,
        rowHeight: Float,
        rowNumber: Int,
        item: QuotationItemEntity,
        paint: TextPaint
    ): Float {
        val y = startY
        var xRight = PAGE_WIDTH - MARGIN

        drawCellText(canvas, PersianNumberFormatter.toPersianDigits(rowNumber.toString()), paint, xRight, y + 4f, COL_ROW_NO)
        xRight -= COL_ROW_NO
        drawCellText(canvas, item.titleSnapshot, paint, xRight, y + 4f, COL_TITLE)
        xRight -= COL_TITLE
        val qtyText = if (item.quantity == item.quantity.toLong().toDouble())
            item.quantity.toLong().toString() else item.quantity.toString()
        drawCellText(canvas, PersianNumberFormatter.toPersianDigits(qtyText), paint, xRight, y + 4f, COL_QTY)
        xRight -= COL_QTY
        drawCellText(canvas, item.unitSnapshot, paint, xRight, y + 4f, COL_UNIT)
        xRight -= COL_UNIT
        drawCellText(canvas, PersianNumberFormatter.formatThousands(item.unitPriceSnapshot), paint, xRight, y + 4f, COL_UNIT_PRICE)
        xRight -= COL_UNIT_PRICE
        drawCellText(canvas, PersianNumberFormatter.formatThousands(item.lineTotal), paint, xRight, y + 4f, COL_TOTAL)

        canvas.drawLine(MARGIN, y + rowHeight, PAGE_WIDTH - MARGIN, y + rowHeight, Paint().apply {
            strokeWidth = 0.4f; color = android.graphics.Color.LTGRAY
        })

        return y + rowHeight
    }

    private fun drawTotalAndFooter(
        canvas: Canvas,
        startY: Float,
        quotation: QuotationEntity,
        headerPaint: TextPaint,
        bodyPaint: TextPaint,
        smallPaint: TextPaint
    ): Float {
        var y = startY + 10f

        y += drawRtlText(
            canvas,
            "جمع کل نهایی: ${PersianNumberFormatter.formatRial(quotation.totalAmount)}",
            headerPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH
        ) + 10f

        quotation.description?.let {
            if (it.isNotBlank()) {
                y += drawRtlText(canvas, "توضیحات: $it", bodyPaint, PAGE_WIDTH - MARGIN, y, CONTENT_WIDTH) + 10f
            }
        }

        y += 20f
        // محل امضا: راست = مشتری، چپ = شرکت (چون صفحه RTL است، امضای شرکت باید سمت چپ ظاهری باشد)
        canvas.drawLine(MARGIN, y, MARGIN + 150f, y, Paint().apply { strokeWidth = 0.7f })
        drawLtrAlignedText(canvas, "مهر و امضای شرکت", smallPaint, MARGIN, y + 4f, 150f)

        canvas.drawLine(PAGE_WIDTH - MARGIN - 150f, y, PAGE_WIDTH - MARGIN, y, Paint().apply { strokeWidth = 0.7f })
        drawRtlText(canvas, "امضای مشتری", smallPaint, PAGE_WIDTH - MARGIN, y + 4f, 150f)

        return y + 20f
    }

    private fun drawPageNumber(canvas: Canvas, pageIndex: Int, totalPages: Int, paint: TextPaint) {
        val text = "صفحه ${PersianNumberFormatter.toPersianDigits(pageIndex.toString())} از ${PersianNumberFormatter.toPersianDigits(totalPages.toString())}"
        drawCenteredText(canvas, text, paint, PAGE_HEIGHT - MARGIN + 12f)
    }

    private fun drawCenteredText(canvas: Canvas, text: String, paint: TextPaint, y: Float) {
        val layout = buildLayout(text, paint, CONTENT_WIDTH)
        val textWidth = paint.measureText(text)
        canvas.save()
        canvas.translate((PAGE_WIDTH - textWidth) / 2f, y)
        layout.draw(canvas)
        canvas.restore()
    }

    private fun drawLtrAlignedText(canvas: Canvas, text: String, paint: TextPaint, leftX: Float, y: Float, maxWidth: Float): Float {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(leftX, y)
        layout.draw(canvas)
        canvas.restore()
        return layout.height.toFloat()
    }

    private fun drawCellText(canvas: Canvas, text: String, paint: TextPaint, rightX: Float, y: Float, width: Float) {
        drawRtlText(canvas, text, paint, rightX, y, width - 4f)
    }

    private fun drawRtlTextHeight(paint: TextPaint): Float {
        val fm = paint.fontMetrics
        return fm.descent - fm.ascent
    }
}
