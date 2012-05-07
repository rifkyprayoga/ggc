package ggc.core.print;

import ggc.core.data.DayValuesData;
import ggc.core.data.MonthlyValues;
import ggc.core.util.DataAccess;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atech.i18n.I18nControlAbstract;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *  Application:   GGC - GNU Gluco Control
 *
 *  See AUTHORS for copyright information.
 * 
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 2 of the License, or (at your option) any later
 *  version.
 * 
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.
 * 
 *  You should have received a copy of the GNU General Public License along with
 *  this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 *  Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *  Filename:     PrintAbstract
 *  Description:  Abstract class for printing via creation of PDF (iText)
 * 
 *  Author: andyrozman {andy@atech-software.com}  
 */

public abstract class PrintAbstract extends PdfPageEventHelper
{
    
    private static Log log = LogFactory.getLog(PrintAbstract.class);

    protected DayValuesData m_data = null;
    protected MonthlyValues m_mv = null;
    protected DataAccess m_da = DataAccess.getInstance();
    protected I18nControlAbstract ic = null;
    String name = "";

    protected BaseFont base_helvetica = null;
    protected BaseFont base_times = null;
    protected Font text_normal = null;
    protected Font text_bold = null;
    protected Font text_italic = null;

    /**
     * Print root must always contain trailing slash, so ../data/print/ is correct, while ../data/print is incorrect.
     * It should be stored under GGC main structure, so that parent of last directory (in this case data) already 
     * exists. And of course unix path divider must be used. (/ instead of \ on windows) 
     */
    private static String print_root = "../data/print/";
    
    
    
    /**
     * Constructor
     * 
     * @param data
     * @param ic
     * @param do_init 
     */
    public PrintAbstract(DayValuesData data, I18nControlAbstract ic, boolean do_init)
    {
        this.m_data = data;
        this.ic = ic;
        
        if (do_init)
            init();
    }

    
     
    /**
     * Constructor
     * 
     * @param mv
     * @param ic
     */
    public PrintAbstract(MonthlyValues mv, I18nControlAbstract ic)
    {
        //this.m_data = data;
        this.m_mv = mv;
        this.ic = ic;
        init();
    }
    
    /**
     * Constructor 
     * 
     * @param ic
     */
    public PrintAbstract(I18nControlAbstract ic)
    {
        this.ic = ic;
        init();
    }
    
    protected void init()
    {
        //name = System.currentTimeMillis();
        createName();

        try
        {
            base_helvetica = BaseFont.createFont("Helvetica", BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
            base_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
            text_normal = new Font(this.base_helvetica , 12, Font.NORMAL);
            text_bold = new Font(this.base_helvetica , 12, Font.BOLD);
            text_italic = new Font(this.base_helvetica , 12, Font.ITALIC);
        }
        catch(Exception ex)
        {
            System.out.println("Exception on font create: " + ex);
        }
        
        createDocument();
        
    }
    
    
    /**
     * Get Name
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns Name of report with Full Path
     * 
     * @return
     */
    public String getNameWithPath()
    {
        File f = new File(print_root + getName());
        return f.getAbsolutePath();
    }
    
    
    /**
     * Returns Name of report with Full Path
     * 
     * @return
     */
    public String getRelativeNameWithPath()
    {
        System.out.println("Rel: " + print_root + getName());
        return print_root + getName();
    }
    
    
    /**
     * Returns report name as File instance
     * 
     * @return
     */
    public File getNameFile()
    {
        File f = new File(print_root + getName());
        return f;
    }
    
    /**
     * Create Name
     */
    public void createName()
    {
        checkIfRootExists();
        
        this.name = this.getFileNameBase() + "_" + this.getFileNameRange() + "_";
        
        for(int i=1; i< Integer.MAX_VALUE; i++)
        {
            File f = new File(PrintAbstract.print_root + this.name + i + ".pdf");
            
            if (!f.exists())
            {
                name += i + ".pdf";
                break;
            }
        }
        
        System.out.println("Name: " + name);
        
    }
    
    /**
     * Get Title
     * 
     * @return
     */
    public abstract Paragraph getTitle();
    

    
    
    /**
     * Create Document
     */
    public void createDocument()
    {

        // step1
        File fl = new File(PrintAbstract.print_root + this.getName());
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try 
        {
            // step2 - asign pdf to file
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fl.getAbsoluteFile()));
            
            // step3 - set page event, open document and put on title
            writer.setPageEvent(this);
            
            document.open();
            document.add(getTitle());

            // step4 - fill body of document
            fillDocumentBody(document);
                

        } 
        catch (Exception de) 
        {
            log.error("Error on document creation [" + de.getMessage() + "]: "+ de, de);
            de.printStackTrace();
        }

        // step5
        document.close();

    }

    
    
    
    
   /* 
    private void SetComment(String text, PdfPTable table)
    {
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase(text, normal_text));
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    }*/

    /*
    private PdfPCell GetPhrasedCell(String text)
    {
	//PdfPHeader h = new P
	/*
        PdfPCell cl = new PdfPCell();
        //cl.setBorder(PdfPCell.BOTTOM|PdfPCell.TOP|PdfPCell.LEFT|PdfPCell.RIGHT);
        cl.setBorderWidth(1);
        cl.setVerticalAlignment(Element.ALIGN_CENTER);
        cl.setHorizontalAlignment(Element.ALIGN_LEFT);
        cl.addElement(new Phrase(text, normal_text));
        */
	
	/*
        return cl;

    } */
    
    
    protected void setBackground(int element_cnt, PdfPTable table)
    {
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BASELINE);
	
        if (element_cnt%2==1) 
            table.getDefaultCell().setGrayFill(0.9f);
        else
            table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
    	
    }
    
 /*   
    private void addEmptyValues(int day, PdfPTable table)
    {
        table.addCell(day + "." + this.m_mv.getMonth());
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
    }
   */ 

    /*
    private void addValues(int entry, int day, DailyValuesRow dvr, PdfPTable table)
    {
	if (entry==0)
	    table.addCell(day + "." + this.m_mv.getMonth());
	else
	    table.addCell("");
	
	
        table.addCell(dvr.getTimeAsString());
        table.addCell(dvr.getBGAsString());
        table.addCell(dvr.getIns1AsString());
        table.addCell(dvr.getIns2AsString());
        table.addCell(dvr.getCHAsString());
        table.addCell(dvr.getUrine());
        //table.addCell(GetPhrasedCell(dvr.getComment()));

        SetComment(dvr.getComment(), table);
    }
    */
    
    
    
    private void checkIfRootExists()
    {
        File f = new File(print_root);
        
        if (!f.exists())
        {
            
//            String s = print_root.substring(0, print_root.lastIndexOf("/"));
//            s = print_root.substring(0, print_root.lastIndexOf("/"));
            
            try
            {
                f.mkdir();
//                File fs = new File(s);
//                f.createNewFile();
            }
            catch(Exception ex)
            {
                log.error("Error creating new print directory ! [" + PrintAbstract.print_root + "]. Ex: " + ex, ex);
            }
        }
        
    }


    
    /**
     * On End Page
     * 
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) 
    {
        try 
        {
            Rectangle page = document.getPageSize();
            PdfPTable foot = new PdfPTable(1);

            PdfPCell pc = new PdfPCell();
            pc.setBorderColor(BaseColor.WHITE);

            Font f = new Font(this.base_times , 10, Font.ITALIC|Font.BOLD); // this.base_times

            pc.setPhrase(new Phrase(new Chunk(ic.getMessage("REPORT_FOOTER"), f)));
            pc.setHorizontalAlignment(Element.ALIGN_CENTER);
            foot.addCell(pc);
            foot.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),
            writer.getDirectContent());
        }
        catch (Exception e) 
        {
            throw new ExceptionConverter(e);
        }
    }

    
    /**
     * Create document body.
     * 
     * @param document
     * @throws Exception
     */
    public abstract void fillDocumentBody(Document document) throws Exception;
    
    /**
     * Returns base filename for printing job, this is just part of end filename (starting part)
     * 
     * @return 
     */
    public abstract String getFileNameBase();
    
    /**
     * Returns data part of filename for printing job, showing which data is being printed
     * 
     * @return 
     */
    public abstract String getFileNameRange();
    

}
