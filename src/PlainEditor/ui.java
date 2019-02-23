package PlainEditor;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.util.Arrays;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import javax.swing.JFrame;

public class ui extends JFrame  {

    public String path="";

    public static boolean saved=false;

    private JComboBox fontComboBox;

    private JComboBox sizeComboBox;

    private JCheckBox boldCheck, italCheck;

    private String[] fonts;

    public JTextField message=new JTextField();

    public JTextPane txt = new JTextPane();

    public int fontSize=18;

    ui()
    {
        util.timeSystem();




        txt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_TAB )
                {
                    util.autoSaveWPM(ui.this);
                }
                util.autoSave(ui.this);
            }
        });

        LinePainter linePainter = new LinePainter(txt,new Color(174, 252, 244));

        try
        {
            UIManager.setLookAndFeel(util.lookAndFeel[4]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        linePainter.setColor(Color.green);

        JPopupMenu jPopupMenu = new JPopupMenu();

        txt.setFont(new Font("Arial",0,fontSize));

        this.add(message,BorderLayout.SOUTH);
        message.setEditable(false);

        txt.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {

               message.setText(stateOf(saved)+"   "+"Line : "+util.getRow(e.getDot(), (JTextComponent)e.getSource())
                        + " Col : "+util.getColumn(e.getDot(), (JTextComponent)e.getSource()));
            }
        });

        txt.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.isControlDown())
                {
                    if(e.getWheelRotation()>0)
                        fontSize++;
                    else if(e.getWheelRotation()<0 )
                        fontSize--;
                    fontSize=util.inRange(fontSize,4,72);

                    txt.setFont(new Font(txt.getFont().getName(),txt.getFont().getStyle(),fontSize));
                }
                else
                {
                    txt.getParent().dispatchEvent(e);
                }

            }
        });

        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.gray));

        setTitle("Plain Editor - " + new Integer(Main.ser).toString());

        JMenuBar jMenuBar=new JMenuBar();

        JMenu fileMenu=new JMenu("File");
        JMenu editMenu=new JMenu("Edit");
        JMenu formatMenu=new JMenu("Format");
        JMenu viewMenu=new JMenu("View");
        JMenu toolMenu=new JMenu("Tools");
        JMenu helpMenu=new JMenu("Help");
        JMenu sampleMenu = new JMenu("Sample");

        jMenuBar.add(fileMenu);
        jMenuBar.add(editMenu);
        jMenuBar.add(formatMenu);
        jMenuBar.add(viewMenu);
        jMenuBar.add(toolMenu);
        jMenuBar.add(helpMenu);
        jMenuBar.add(sampleMenu);

        JMenuItem newItem = new JMenuItem("New");

        ActionListener newFileAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.newFile();
            }
        };

        newItem.addActionListener(newFileAction);

        txt.registerKeyboardAction( newFileAction , KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);

        JMenuItem openItem = new JMenuItem("Open");

        ActionListener openAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui temUI=new ui();
                util.openFile(temUI);
            }
        };

        openItem.addActionListener(openAction);

        txt.registerKeyboardAction(openAction,KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem saveItem = new JMenuItem("Save");

        ActionListener saveAction =new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                util.saveFile(ui.this);
            }
        };

        saveItem.addActionListener(saveAction);

        txt.registerKeyboardAction(saveAction,KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem printItem = new JMenuItem("To PDF");
        printItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.printFile(ui.this);
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");

        ActionListener exitAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.exitFile(ui.this);
            }
        };

        exitItem.addActionListener(exitAction);

        JMenuItem exitAllItem = new JMenuItem("Exit All");

        ActionListener exitAllAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.exitAllFile();
            }
        };

        exitAllItem.addActionListener(exitAllAction);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        fileMenu.add(exitAllItem);

        JMenuItem cutItem = new JMenuItem("Cut");
        ActionListener cutAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.cutEdit(txt);
            }
        };

        cutItem.addActionListener(cutAction);

        txt.registerKeyboardAction(cutAction,KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem copyItem = new JMenuItem("Copy");

        ActionListener copyAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.copyEdit(txt);
            }
        };

        copyItem.addActionListener(copyAction);

        txt.registerKeyboardAction(copyAction,KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem pasteItem = new JMenuItem("Paste");

        ActionListener pasteAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    util.pasteEdit(txt);
                }
                catch (Exception ex)
                {

                }
            }
        };

        pasteItem.addActionListener(pasteAction);

        txt.registerKeyboardAction(pasteAction,KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem deleteItem = new JMenuItem("Delete");

        ActionListener deleteAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.deleteEdit(txt);
            }
        };

        deleteItem.addActionListener(deleteAction);

        JMenuItem findItem = new JMenuItem("Find");
        ActionListener findAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Find.findView(txt);
            }
        };
        findItem.addActionListener(findAction);
        txt.registerKeyboardAction(findAction,KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem replaceItem = new JMenuItem("Replace");
        ActionListener replaceAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.replaceEdit(txt);
            }
        };
        replaceItem.addActionListener(replaceAction);
        txt.registerKeyboardAction(replaceAction,KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem gotoItem = new JMenuItem("Goto");
        ActionListener gotoAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.gotoEdit(txt);
            }
        };
        gotoItem.addActionListener(gotoAction);
        txt.registerKeyboardAction(gotoAction,KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.CTRL_MASK),JComponent.WHEN_FOCUSED);

        JMenuItem selectAllItem = new JMenuItem("Select All");

        selectAllItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.selectAllEdit(txt);
            }
        });

        JMenuItem timeDateItem = new JMenuItem("Time/Date Stamp");

        timeDateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.timeDateEdit(txt);
            }
        });

        JMenuItem undoItem=new JMenuItem("Undo");
        JMenuItem redoItem=new JMenuItem("Redo");

        final UndoManager undo = new UndoManager();
        Document doc = txt.getDocument();

        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undo.addEdit(evt.getEdit());
            }
        });

        txt.getActionMap().put("Undo", new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });

        txt.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        txt.getActionMap().put("Redo", new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

        txt.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undo.undo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undo.redo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(replaceItem);
        editMenu.add(gotoItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        editMenu.add(timeDateItem);

        JMenuItem fontItem = new JMenuItem("Font");

        fontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FontStyleSizeFamilyChooser();
            }
        });

        formatMenu.add(fontItem);

        JMenuItem readingModeItem=new JMenuItem("Reading Mode");

        readingModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.readingMode(txt);
            }
        });

        viewMenu.add(readingModeItem);

        JMenuItem byteCodeItem=new JMenuItem("How java works - this instance");

        byteCodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    ToClassFile.toClassFile(txt);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem executeItem=new JMenuItem("Execute");
        executeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Execute.runProgramme(txt);
            }
        });

        JMenuItem recoverItem=new JMenuItem("Recover files");
        recoverItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    util.recoverFiles(Main.folder);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem wpmItem=new JMenuItem("Check PE WPM");
        wpmItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WPM.calculate();
            }
        });

        toolMenu.add(byteCodeItem);
        toolMenu.add(executeItem);
        toolMenu.add(recoverItem);
        toolMenu.add(wpmItem);

        JMenuItem viewHelpItem = new JMenuItem("View Help");
        JMenuItem aboutNotepadItem = new JMenuItem("About Plain Editor");
        aboutNotepadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.aboutPE();
            }
        });

        viewHelpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.helpShow();
            }
        });

        helpMenu.add(viewHelpItem);
        helpMenu.add(aboutNotepadItem);

        setJMenuBar(jMenuBar);

        JScrollPane scroller = new JScrollPane(txt);
        txt.setEditorKit(new javax.swing.text.StyledEditorKit());

        TextLineNumber lineNumber = new TextLineNumber(txt,1);

        txt.add(lineNumber);

        scroller.setRowHeaderView(lineNumber );
        txt.requestFocus();

        add(scroller, BorderLayout.CENTER);

        setBounds(100,100,600,400);

        JFrame jFrame = new JFrame();

        setPreferredSize(new Dimension(600,400));

        setVisible(true);

        JMenuItem popCopyItem = new JMenuItem("Copy");
        popCopyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.copyEdit(txt);
            }
        });

        jPopupMenu.add(popCopyItem);

        JMenuItem popCutItem= new JMenuItem("Cut");
        popCutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.cutEdit(txt);
            }
        });

        jPopupMenu.add(popCutItem);

        JMenuItem popPasteItem=new JMenuItem("Paste");
        popPasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                util.pasteEdit(txt);
            }
        });

        jPopupMenu.add(popPasteItem);

        jPopupMenu.addSeparator();

        jPopupMenu.add(selectAllItem);

        jPopupMenu.addSeparator();

        JMenuItem popUndo=new JMenuItem("Undo");
        JMenuItem popRedo=new JMenuItem("Redo");

        popUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undo.undo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        popRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undo.redo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        jPopupMenu.add(popUndo);
        jPopupMenu.add(popRedo);

        txt.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3)
                {
                    jPopupMenu.show(txt,e.getX(),e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        txt.setCaretPosition(0);
    }

    private class FontListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateText();
        }

        public void updateText() {
            String name = (String) fontComboBox.getSelectedItem();
            Integer size = (Integer) sizeComboBox.getSelectedItem();
            int style;
            if (boldCheck.isSelected() && italCheck.isSelected())
                style = Font.BOLD | Font.ITALIC;
            else if (boldCheck.isSelected())
                style = Font.BOLD;
            else if (italCheck.isSelected())
                style = Font.ITALIC;
            else
                style = Font.PLAIN;
            Font f = new Font(name, style, size.intValue());
            txt.setFont(f);
        }
    }

    public void FontStyleSizeFamilyChooser() {
        JFrame jFrame=new JFrame("Font Chooser");

        jFrame.setLocationRelativeTo(txt);

        jFrame.setSize(500,75);

        jFrame.setResizable(false);

        FontListener fl = new FontListener();
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = g.getAvailableFontFamilyNames();

        Arrays.sort(fonts);

        JPanel controlPanel = new JPanel();

        fontComboBox = new JComboBox(fonts);
        fontComboBox.addActionListener(fl);
        controlPanel.add(new JLabel("Family: "));
        controlPanel.add(fontComboBox);

        Integer[] sizes = { 7, 8, 9, 10, 11, 12, 14, 18, 20, 22, 24, 36 ,40,50,60,72};

        sizeComboBox = new JComboBox(sizes);
        sizeComboBox.setSelectedIndex(7);
        sizeComboBox.addActionListener(fl);
        controlPanel.add(new JLabel("Size: "));
        controlPanel.add(sizeComboBox);

        boldCheck = new JCheckBox("Bold");
        boldCheck.addActionListener(fl);
        controlPanel.add(boldCheck);

        italCheck = new JCheckBox("Italic");
        italCheck.addActionListener(fl);
        controlPanel.add(italCheck);

        fl.updateText();

        jFrame.add(controlPanel);

        jFrame.setVisible(true);
    }

    String stateOf(boolean x)
    {
        if(x==true)
            return "Saved";
        return "Not Saved";
    }
}