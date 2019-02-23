package PlainEditor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class Find {

    public static void findView(JTextPane jTextPane)
    {
        JFrame jFrame = new JFrame("Find");
        jFrame.setLocationRelativeTo(jTextPane);
        jFrame.setAlwaysOnTop(true);

        jFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                Find.removeHighlights(jTextPane);
                e.getWindow().dispose();
            }
        });
        jFrame.setBounds(150,150,250,100);
        jFrame.setResizable(false);
        JPanel jPanel=new JPanel();
        jFrame.add(jPanel,BorderLayout.SOUTH);
        JTextField jTextField = new JTextField("",15);

        jTextField.setEditable(true);
        JCheckBox jCheckBox=new JCheckBox("Match case");
        JCheckBox jCheckBox1=new JCheckBox("Is word");
        jCheckBox1.setVisible(true);
        jCheckBox.setVisible(true);
        jFrame.add(jTextField,BorderLayout.NORTH);
        jPanel.add(jCheckBox,BorderLayout.EAST);
        jPanel.add(jCheckBox1,BorderLayout.NORTH);
        jFrame.setVisible(true);

        jTextPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                findNow(jTextPane,jTextField,jCheckBox,jCheckBox1);
            }
        });

        jCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                findNow(jTextPane,jTextField,jCheckBox,jCheckBox1);

            }
        });

        jCheckBox1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                findNow(jTextPane,jTextField,jCheckBox,jCheckBox1);
            }
        });

        jTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
            @Override
            public void keyPressed(KeyEvent e) {
                if(jTextField.getText().equals("") || jTextField.getText()==null)
                {
                    removeHighlights(jTextPane);
                    return;
                }
                if(e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    if(jTextField.getText()==null || jTextField.getText()=="")
                    {
                        jFrame.dispose();
                        return;
                    }
                    try{
                        Find.highlight(jTextPane,jTextField.getText().toString(),jCheckBox.isSelected(),jCheckBox1.isSelected());

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Find.highlight(jTextPane,jTextField.getText().toString(),jCheckBox.isSelected(),jCheckBox1.isSelected());

            }
        });
    }

    public static String[] type={" ","\n","\b","\r"};

    public static void highlight(JTextComponent textComp, String pattern,boolean caseMatch,boolean isWord)
    {
        if(pattern==null || pattern.equals(""))
            return;
        removeHighlights(textComp);

        try
        {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            if(caseMatch==true)
            {
                if(isWord==true)
                {
                    for(int i=0;i<4;i++)
                    {
                        if(text.indexOf(pattern+type[i], pos) == 0)
                        {
                            hilite.addHighlight(0, pos+pattern.length(), myHighlightPainter);
                        }
                    }
                }
                else
                {
                    if((pos = text.indexOf(pattern, pos)) == 0)
                    {
                        hilite.addHighlight(0, pos+pattern.length(), myHighlightPainter);
                    }
                }
            }
            else
            {
                if(isWord==true)
                {
                    for(int i=0;i<4;i++)
                    {

                        if(text.toUpperCase().indexOf(pattern.toUpperCase()+type[i], pos) == 0)
                        {
                            hilite.addHighlight(0, pos+pattern.length(), myHighlightPainter);
                        }
                    }
                }
                else
                {
                    if((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) == 0)
                    {
                        hilite.addHighlight(0, pos+pattern.length(), myHighlightPainter);
                    }
                }
            }
            if(caseMatch==true)
            {
                if(isWord==true)
                {
                    for(int i=0;i<4;i++)
                    {
                        for(int j=0;j<4;j++)
                        {
                            while ((pos = text.indexOf(type[i]+pattern+type[j], pos)) >= 0)
                            {
                                hilite.addHighlight(pos+1, pos+pattern.length()+1, myHighlightPainter);
                                pos += pattern.length();
                            }
                        }
                    }
                }
                else
                {
                    while ((pos = text.indexOf(pattern, pos)) >= 0)
                    {
                        textComp.select(pos,pos+pattern.length());
                        hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                        pos += pattern.length();
                    }
                }

            }
            else
            {
                if(isWord==true)
                {

                    for(int i=0;i<4;i++)
                    {
                        for(int j=0;j<4;j++)
                        {
                            while ((pos = text.toUpperCase().indexOf(type[i] +pattern.toUpperCase()+type[j], pos)) >= 0)
                            {
                                hilite.addHighlight(pos+1, pos+pattern.length()+1, myHighlightPainter);
                                pos += pattern.length();
                            }
                        }
                    }
                }
                else
                {
                    while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0)
                    {
                        hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                        pos += pattern.length();
                    }
                }

            }

        }
        catch (BadLocationException e) {

        }
    }

    public static void removeHighlights(JTextComponent textComp)
    {
        Highlighter.Highlight[] hilites = textComp.getHighlighter().getHighlights();
        for (int i=0; i<hilites.length; i++)
        {
            if (hilites[i].getPainter() instanceof MyHighlightPainter)
            {
                textComp.getHighlighter().removeHighlight(hilites[i]);
            }
        }
    }

    static Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.orange);

    static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        public MyHighlightPainter(Color color)
        {
            super(color);
        }
    }

    static void findNow(JTextPane jTextPane,JTextField jTextField,JCheckBox jCheckBox,JCheckBox jCheckBox1)
    {
        if(jTextField.getText().equals("") || jTextField.getText()==null)
        {
            removeHighlights(jTextPane);
            return;
        }
        Find.highlight(jTextPane,jTextField.getText().toString(),jCheckBox.isSelected(),jCheckBox1.isSelected());
    }
}