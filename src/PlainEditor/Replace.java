package PlainEditor;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.Highlighter;

import static PlainEditor.Find.myHighlightPainter;
import static PlainEditor.Find.removeHighlights;

public class Replace {
        public static int pos=0;

        public static void replaceView(JTextPane jTextPane)
        {
            JFrame jFrame=new JFrame("Replace");
            jFrame.setLocationRelativeTo(jTextPane);
            jFrame.setBounds(150,150,600,100);

            jFrame.setVisible(true);

            jFrame.setResizable(false);
            JPanel jPanel1=new JPanel();
            JPanel jPanel2=new JPanel();
            jFrame.add(jPanel1,BorderLayout.NORTH);
            jFrame.add(jPanel2,BorderLayout.SOUTH);
            JTextField jTextField1=new JTextField("",15);

            jTextField1.setToolTipText("Find");
            if(jTextPane.getSelectedText()!=null)
            {
                jTextField1.setText(jTextPane.getSelectedText());
            }
            JTextField jTextField2=new JTextField("",15);

            jTextField2.setToolTipText("Replace");
            jPanel1.add(jTextField1);
            jPanel1.add(jTextField2);
            JButton replaceAll=new JButton("Replace All");
            replaceAll.setSize(50,20);
            JButton replaceThis=new JButton("Replace Selected");
            replaceThis.setSize(50,20);
            JCheckBox matchCase=new JCheckBox("Match Case");
            JCheckBox isWord=new JCheckBox("Is Word");
            jPanel2.add(matchCase);
            jPanel2.add(isWord);
            jPanel2.add(replaceThis);
            jPanel2.add(replaceAll);

            BasicArrowButton up=new BasicArrowButton(BasicArrowButton.NORTH);
            BasicArrowButton down = new BasicArrowButton(BasicArrowButton.SOUTH);

            up.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    markPrev(jTextPane,jTextField1.getText());
                }
            });

            down.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    markNext(jTextPane,jTextField1.getText());
                }
            });

            replaceThis.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try
                    {
                        jTextPane.select(pos,pos+jTextField1.getText().length());
                        if(jTextPane.getSelectedText().equals(jTextField1.getText()))
                        {
                            jTextPane.replaceSelection(jTextField2.getText());
                        }
                        else
                        {
                            jTextPane.select(pos,pos);
                            return;
                        }
                    }
                    catch (Exception e1)
                    {
                        jTextPane.setCaretPosition(0);
                        e1.printStackTrace();
                    }
                }
            });

            jPanel2.add(up);
            jPanel2.add(down);
            jFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    removeHighlights(jTextPane);
                    e.getWindow().dispose();
                }
            });
            replaceAll.addActionListener(new ActionListener() {
                int cou=0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    Find.highlight(jTextPane,jTextField1.getText(),matchCase.isSelected(),isWord.isSelected());
                    JFrame jFrame1 = new JFrame("Replace All ? ");
                    jFrame1.setBounds(50,50,100,100);
                    jFrame1.setResizable(false);
                    JButton ok=new JButton("OK");
                    ok.setBackground(new Color(237, 94, 42));
                    jFrame1.add(ok,BorderLayout.CENTER);
                    ok.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            removeHighlights(jTextPane);
                            replaceAllFunc(jTextPane,jTextField1.getText(),jTextField2.getText());
                            jFrame1.dispose();
                            jFrame.dispose();
                        }
                    });
                    jFrame1.setVisible(true);
                    jFrame1.addWindowListener(new WindowAdapter()
                    {
                        @Override
                        public void windowClosing(WindowEvent e)
                        {
                            e.getWindow().dispose();
                        }
                    });
                }
            });
        }

        public static void replaceAllFunc(JTextPane jTextPane,String from,String to)
        {
            if(from==null || from.equals("") || to==null || to.equals(""))
                return ;
            jTextPane.setText(jTextPane.getText().replaceAll(from,to));
        }

        public static void replace(JTextPane jTextPane,String from,String to)
        {
            Find.highlight(jTextPane,from,true,true);
        }

        public static void markNext(JTextPane jTextPane,String pattern)
        {
            removeHighlights(jTextPane);
            if(pattern.equals("") || pattern==null)
                return ;
            jTextPane.select(pos,pos+pattern.length());
            try
            {
                if(pos==-1)
                    pos=0;
                if(jTextPane.getSelectedText().equals(pattern) || jTextPane.getSelectedText()==null)
                    pos++;
                else if(jTextPane.getSelectedText()==null )
                {
                    pos=0;
                }
                else
                    jTextPane.select(pos,pos);

                Highlighter hilite = jTextPane.getHighlighter();
                Document doc = jTextPane.getDocument();
                String text = doc.getText(0, doc.getLength());
                if(((pos = text.indexOf(pattern, pos)) >= 0))
                {
                    removeHighlights(jTextPane);
                    System.out.println(pos+ " +++  "+(pos+pattern.length()));
                    hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                }
            }
            catch (Exception e)
            {
                jTextPane.setCaretPosition(0);
                e.printStackTrace();
            }
        }

        public static void markPrev(JTextPane jTextPane,String pattern)
        {
            if(pos==-1)
            {
                pos=jTextPane.getDocument().getLength()-1;
            }
            removeHighlights(jTextPane);
            if(pattern.equals("") || pattern==null)
                return ;
            jTextPane.select(pos,pos+pattern.length());
            try
            {
                if(jTextPane.getSelectedText().equals(pattern) && pos>-1)
                    pos--;
                else
                    jTextPane.select(pos,pos);

                Highlighter hilite = jTextPane.getHighlighter();
                Document doc = jTextPane.getDocument();
                String text = doc.getText(0, doc.getLength());
                if(((pos = text.lastIndexOf(pattern,pos)) >= 0))
                {
                    removeHighlights(jTextPane);
                    hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                }
            }
            catch (Exception e)
            {
                jTextPane.setCaretPosition(0);
                e.printStackTrace();
            }
        }
}
