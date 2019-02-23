package PlainEditor;

import javax.print.Doc;
import javax.print.SimpleDoc;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.print.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.Toolkit;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.io.FileInputStream;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import static PlainEditor.ui.saved;

public class util
{

    static Color lightGray=new Color(192,192,192);

    public static Color[] backFront ={new Color(255,255,255),new Color(51,51,51)};

    public static int printerNumber=1;

    static Color[] lineMarkerColor = { new Color(174, 252, 244) , Color.green};

    public static void newFile()
    {
        Main.ser++;
        new ui();
    }
    public static void openFile(ui ui)
    {
        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.setDialogTitle("Open File in Plain Editor");
        jFileChooser.setApproveButtonText("Open in Plain Editor");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PE Supported Files",
                "txt", "text","TXT","JAVA","java","c","cpp","C","CPP");
        jFileChooser.setFileFilter(filter);

        int i=jFileChooser.showOpenDialog(ui);
        if(i==JFileChooser.APPROVE_OPTION){
            File f=jFileChooser.getSelectedFile();
            String filepath=f.getPath();
            try{
                BufferedReader br=new BufferedReader(new FileReader(filepath));
                String s1="",s2="";
                while((s1=br.readLine())!=null){
                    s2+=s1+"\n";
                }
                ui.txt.setText(s2);
                br.close();
                ui.setTitle(ui.getTitle()+  "  "+filepath);
                ui.message.setText("saved");
                ui.path=filepath;
            }catch (Exception ex) {ex.printStackTrace();  }

        }
        else if(i==JFileChooser.CANCEL_OPTION)
        {
            exitFile(ui);
        }
    }

    public static void openFile(ui ui,File file)
    {
        Main.ser++;
        try{
            BufferedReader br=new BufferedReader(new FileReader(file));
            String s1="",s2="";
            while((s1=br.readLine())!=null){
                s2+=s1+"\n";
            }
            ui.txt.setText(s2);
            br.close();
        }catch (Exception ex) {ex.printStackTrace();  }
    }

    public static void exitFile(ui ui)
    {
        ui.dispatchEvent(new WindowEvent(ui, WindowEvent.WINDOW_CLOSING));
    }

    public static void saveFile(ui ui)
    {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setVisible(true);

        jFileChooser.setDialogTitle("Save File in Device");

        FileNameExtensionFilter txt = new FileNameExtensionFilter("Text Files","txt", "text","TXT");
        FileNameExtensionFilter c = new FileNameExtensionFilter("C Files","c", "C");
        FileNameExtensionFilter cpp = new FileNameExtensionFilter("CPP/C++ Files","cpp", "CPP");
        FileNameExtensionFilter java = new FileNameExtensionFilter("Java Files","java", "JAVA");
        FileNameExtensionFilter otherFormat = new FileNameExtensionFilter("Other Format","Other Format");

        jFileChooser.setFileFilter(c);
        jFileChooser.setFileFilter(cpp);
        jFileChooser.setFileFilter(java);
        jFileChooser.setFileFilter(otherFormat);
        jFileChooser.setFileFilter(txt);

        int returnVal = jFileChooser.showSaveDialog(ui);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fullPath=jFileChooser.getSelectedFile().getAbsolutePath();
            if(jFileChooser.getFileFilter().getDescription()=="Text Files")
                fullPath+=".txt";
            else if(jFileChooser.getFileFilter().getDescription()=="C Files")
                fullPath+=".c";
            else if(jFileChooser.getFileFilter().getDescription()=="CPP/C++ Files")
                fullPath+=".cpp";
            else if(jFileChooser.getFileFilter().getDescription()=="Java Files")
                fullPath+=".java";
            File file = new File(fullPath);
            PrintWriter writer;
            try{
                writer = new PrintWriter(fullPath, "UTF-8");
                writer.print(ui.txt.getText());
                writer.close();
                if(saved==false)
                    ui.setTitle(ui.getTitle()+"  "+fullPath);
                saved=true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        ui.message.setText("Saved");
    }

    public static void updateStatus(JTextField message,CaretEvent e)
    {
        message.setText(stateOf(saved)+"   "+"Line : "+util.getRow(e.getDot(), (JTextComponent)e.getSource())
                + " Col : "+util.getColumn(e.getDot(), (JTextComponent)e.getSource()));
    }

    static String stateOf(boolean x)
    {
        if(x==true)
            return "Saved";
        return "Not Saved";
    }

    public static void printFile(ui ui)
    {
        try {
            FileInputStream in = new FileInputStream(PDFT.toPdf(ui.txt));
            Doc doc = new SimpleDoc(in, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();

            try {
                service.createPrintJob().print(doc, null);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void exitAllFile()
    {
        System.exit(0);
    }

    public static void replaceEdit(JTextPane jTextPane)
    {
        Replace.replaceView(jTextPane);
    }

    public static void cutEdit(JTextPane jTextPane)
    {
        String myString = jTextPane.getSelectedText();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        jTextPane.replaceSelection("");
    }

    public static void copyEdit(JTextPane jTextPane)
    {
        String myString = jTextPane.getSelectedText();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void deleteEdit(JTextPane jTextPane)
    {
        if(jTextPane.getSelectedText()==null || jTextPane.getSelectedText()=="")
            return ;
        else
            jTextPane.replaceSelection("");
    }

    public static void wordWrapEdit(JTextPane jTextPane)
    {

    }

    public static void selectAllEdit(JTextPane jTextPane)
    {
        jTextPane.selectAll();
    }

    public static void pasteEdit (JTextPane jTextPane)
    {
        String data="";
        Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
        try
        {
            data=(String) c.getData(DataFlavor.stringFlavor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(jTextPane.getSelectedText()==null || jTextPane.getSelectedText()=="")
        {
            insertAtPos(jTextPane,jTextPane.getCaretPosition(),data);
        }
        else
        {
            deleteEdit(jTextPane);
            insertAtPos(jTextPane,jTextPane.getCaretPosition(),data);
        }
    }

    public static void insertAtPos(JTextPane jTextPane,int pos,String data)
    {

        StyledDocument doc = jTextPane.getStyledDocument();
        try{
            doc.insertString(pos,data,jTextPane.getInputAttributes());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void timeDateEdit(JTextPane jTextPane)
    {
        insertAtPos(jTextPane,jTextPane.getCaretPosition(),"\n"+timeStamp());
    }

    public static String timeStamp()
    {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }

    public static void gotoEdit(JTextPane jTextPane)
    {
        JFrame jFrame = new JFrame("Goto");
        jFrame.setLocationRelativeTo(jTextPane);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(300,80);
        jFrame.setResizable(false);

        JTextField lineNumber=new JTextField("");
        lineNumber.setBounds(50,50,100,50);
        lineNumber.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.red));

        jFrame.add(lineNumber);

        lineNumber.setVisible(true);

        JButton jButton = new JButton("Go");

        jButton.setSize(100,50);
        jButton.setVisible(true);

        jFrame.add(jButton,BorderLayout.SOUTH);

        lineNumber.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    int line=getLineAtCaret(jTextPane);
                    try
                    {
                        line=Integer.parseInt(lineNumber.getText());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    gotoEditInternal(jTextPane,line);
                    jFrame.dispose();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int line=getLineAtCaret(jTextPane);
                try
                {
                    line=Integer.parseInt(lineNumber.getText());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                gotoEditInternal(jTextPane,line);
                jFrame.dispose();
            }
        });

        jFrame.setVisible(true);
    }

    public static void gotoEditInternal(JTextPane jTextPane,int line)
    {
        gotoStartOfLine(jTextPane,line);
    }

    public static void gotoStartOfLine(JTextComponent component, int line)
    {
        Element root = component.getDocument().getDefaultRootElement();
        line = Math.max(line, 1);
        line = Math.min(line, root.getElementCount());
        component.setCaretPosition( root.getElement( line - 1 ).getStartOffset() );
    }

    public static int getRow(JTextComponent editor)
    {
        return getRow(editor.getCaretPosition(),editor);
    }

    public static int getRow(int pos, JTextComponent editor)
    {
        int rn = (pos==0) ? 1 : 0;
        try {
            int offs=pos;
            while( offs>0) {
                offs=Utilities.getRowStart(editor, offs)-1;
                rn++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rn;
    }

    public static int getColumn(int pos, JTextComponent editor)
    {
        try {
            return pos-Utilities.getRowStart(editor, pos)+1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getLineAtCaret(JTextComponent component)
    {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        return root.getElementIndex( caretPosition ) + 1;
    }
    public static int getLines(JTextComponent component)
    {
        Element root = component.getDocument().getDefaultRootElement();
        return root.getElementCount();
    }

    public static int inRange( int value, int min,int max)
    {
        if(value<min)
            value=min;
        else if(value>max)
            value=max;
        return value;
    }

    public static void readingMode(JTextPane jTextPane)
    {
        jTextPane.setEditable(!jTextPane.isEditable());
    }

    public static Window findWindow(Component c) {
        if (c == null) {
            return JOptionPane.getRootFrame();
        } else if (c instanceof Window) {
            return (Window) c;
        } else {
            return findWindow(c.getParent());
        }
    }

    public static String[] lookAndFeel= {
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            "javax.swing.plaf.metal.MetalLookAndFeel",
            "sun.awt.noerasebackground",
            "javax.swing.plaf.nimbus.NimbusLookAndFeel"
            };

    public static void timeSystem()
    {
        LocalDateTime now = LocalDateTime.now();
    }

    public static String insert(String in,int pos,String add)
    {
        return in.substring(0,pos)+add+in.substring(pos);
    }
    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }

    public static String firstSpace(String data,int len)
    {
        if(data.length()>=len)
            return data;
        while(data.length()!=len)
            data=insert(data,0," ");
        return data;
    }

    public static void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    public static void appendToPane(JTextPane tp, String msg, Color c,Color f)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset;
        aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, f);
        aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    public static void appendWithStyle(JTextPane tp,String s1,String s2)
    {
        appendToPane(tp,s1,Color.black);
        appendToPane(tp,s2,Color.green);
    }
    public static void appendWithStyle(JTextPane tp,String s1,String s2,boolean x)
    {
        appendToPane(tp,s1,Color.black);
        if(x==true)
            appendToPane(tp,s2,Color.red);
        else
            appendToPane(tp,s2,Color.orange);
    }
    public static void appendWithStyle(JTextPane tp,String s1,String s2,String s3,String s4)
    {
        appendToPane(tp,s1,Color.black);
        appendToPane(tp,s2,Color.green);
        appendToPane(tp,s3,Color.orange);
        appendToPane(tp,s4,Color.red);
    }

    public static void appendWithStyle(JTextPane tp,String s1,String s2,String s3,String s4,boolean com)
    {

        if(com==true)
        {
            appendToPane(tp,s1,Color.black,Color.lightGray);
            appendToPane(tp,s2,Color.green,Color.lightGray);
            appendToPane(tp,s3,Color.orange,Color.lightGray);
            appendToPane(tp,s4,Color.red,Color.lightGray);
        }
        else
        {

            appendToPane(tp,s1,Color.black,Color.white);
            appendToPane(tp,s2,Color.green,Color.white);
            appendToPane(tp,s3,Color.orange,Color.white);
            appendToPane(tp,s4,Color.red,Color.white);
        }

    }
    
    public static void aboutPE()
    {
        JFrame about=new JFrame("About Plain Editor");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        about.setSize(800,500);
        about.setLocation(dim.width/2-about.getSize().width/2, dim.height/2-about.getSize().height/2);
        JTextPane details=new JTextPane();
        JScrollPane sp = new JScrollPane(details);
        about.getContentPane().add( sp );
        appendWithStyle(details,"Plain Editor","\n\n\n\nWhether you’re a developer or a writer, a good text editor is a must-have on any computer, in any operating system.\n" +
                "Plain Editor contains all these features.\n" +
                "Plain Editor is not only text editor it is also IDE for JAVA\n" +
                "Special Features - \n\tMark current line\n" +
                "\tShow line number to get rid of distraction and column number too\n " +
                "\tFind as user type\n " +
                "\tMark all requsted keyword even when replacing\n " +
                "\n\nThis project was made as requirment of course CSE-312\n\n" +
                "Course teacher - Md. Mahbubul Islam (Assistant Professor)\n" +
                "Project coordinator - Arif Reza\n" +
                "Project members- \n" +
                "\tMaifee Ul Asad\n" +
                "\tMonjurul Hasan Sakeer\n" +
                "\tMd. Rizuan\n" +
                "\tSajib Kumar Das\n" +
                "\tMd. Rashedul Alam Anik\n\n" +
                "\t  -------------------------",true);
        details.setEditable(false);
        about.setResizable(false);
        about.setVisible(true);
    }

    public static void helpShow()
    {
        JFrame about=new JFrame("About Plain Editor");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        about.setSize(800,500);
        about.setLocation(dim.width/2-about.getSize().width/2, dim.height/2-about.getSize().height/2);
        JTextPane help=new JTextPane();
        JScrollPane sp = new JScrollPane(help);
        about.getContentPane().add( sp );
        appendToPane(help,"\t\tHelp\n\n\n\t-----------\n\n",Color.RED);
        appendWithStyle(help,"File","\tNew (Ctrl+Shift+N) - Cretaes new file\n\tOpen(Ctrl+O) - Open a file in a new instance\n\tSave(Ctrl+S) - Save this file\n\tPrint(Ctrl+P) - Print this document converting into PDF\n\tExit(Alt+F4) - Exit this instance\n\tExit All - Exit all instance\n");
        appendToPane(help,"\n\n\n",Color.GREEN);
        appendWithStyle(help,"Edit","\tUndo (Ctrl+Z)\n\tRedo(Ctrl+Shift+Z)\n\tCut(Ctrl+X)\n\tCopy(Ctrl+C)\n\tPaste(Ctrl+V)\n\tDelete(Delete/Insert)\n\tFind(Ctrl+F)\n\tReplace(Ctrl+R)\n\tGo to\n\tTime/Date Stamp");
        appendToPane(help,"\n\n\n",Color.GREEN);
        appendWithStyle(help,"Format","\tFont - Select font/style/size from a huge collection\n");
        appendToPane(help,"\n\n\n",Color.GREEN);
        appendWithStyle(help,"View","\tReading Mode - Enable/Disable editing\n\t");
        appendToPane(help,"\n\n\n",Color.GREEN);
        appendWithStyle(help,"Tools","\tHow Java works-this instance - Shows Byte-code, Helpful for understanding JAVA\n\tIDE - Run code(After you are done type and enter \"exit()\"(without quotes))\n");
        appendToPane(help,"\n\n\n",Color.GREEN);
        appendWithStyle(help,"Help","\tView Help\n\tAbout Plain Editor\n\t");
        appendToPane(help,"\n\n\n",Color.GREEN);
        help.setCaretPosition(0);
        help.setEditable(false);
        about.setResizable(false);
        about.setVisible(true);
    }

    public static void recoverFiles(final File folder) {
        try {
            for (final File fileEntry : folder.listFiles()) {
                if (!fileEntry.isDirectory()) {
                    ui ui=new ui();
                    openFile(ui,fileEntry);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void autoSave(ui ui)
    {
        if(ui.path==null || ui.path=="")
        {

            String tem=new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

            String fullPath=Main.folder + "\\" + tem;

            File file = new File(fullPath);
            PrintWriter writer;
            try{
                writer = new PrintWriter(fullPath, "UTF-8");
                writer.print(ui.txt.getText());
                writer.close();
                saved=true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            ui.path=fullPath;
        }
        else
        {
            File file = new File(ui.path);
            PrintWriter writer;
            try{
                writer = new PrintWriter(ui.path, "UTF-8");
                writer.print(ui.txt.getText());
                writer.close();
                saved=true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void autoSaveWPM(ui ui)
    {
            String tem=new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

            String fullPath=Main.wpmFolder + "\\" + tem;

            File file = new File(fullPath);

            PrintWriter writer;
            try{

                writer = new PrintWriter(fullPath, "UTF-8");
                writer.print("\n");
                writer.close();
                saved=true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            ui.path=fullPath;
    }
}
