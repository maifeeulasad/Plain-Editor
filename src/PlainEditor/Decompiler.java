package PlainEditor;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import static PlainEditor.util.*;

public class Decompiler {

    private DataInputStream dataInputStream;
    private String[] pool_specific_values;

    private String res="";
    JTextPane tp = new JTextPane();

    private static boolean color=true;

    public Decompiler(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public Decompiler(String path)
    {
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(path)));
            Decompiler decompiler = new Decompiler(dataInputStream);
            decompiler.analyse();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JFrame errors=new JFrame("Errors");

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            errors.setSize(500,500);
            errors.setLocation(dim.width/2-errors.getSize().width/2, dim.height/2-errors.getSize().height/2);
            JTextPane errorText=new JTextPane();
            JScrollPane sp = new JScrollPane(errorText);
            errors.getContentPane().add( sp );
            if(ToClassFile.err=="true"){

                appendWithStyle(errorText,"Error Accourd!\nGiven below : \n",e.toString());
                errorText.setEditable(false);
                errors.setVisible(true);
            }
            else
            {
                appendWithStyle(errorText,"Error Accourd!\nGiven below : \n","---");
                errorText.setEditable(false);
                errors.setVisible(true);
            }
        }
    }

    public void analyse() {
        JFrame frame = new JFrame("How java works - this instance");
        JScrollPane sp = new JScrollPane(tp);
        frame.add( sp );
        frame.pack( );
        try
        {
            UIManager.setLookAndFeel(util.lookAndFeel[4]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            int magic = readMagic();
            appendWithStyle(tp,"Magic code is: ","0x"+Integer.toHexString(magic).toUpperCase()+"\n");
            double version = readVersion();
            appendWithStyle(tp,"Version is: ",new Double(version).toString()+"\n");

            readConstantContents();

            String accessFlag = readAccessFlag();
            appendWithStyle(tp,"AccessFlag is: ",accessFlag+"\n");
            String thisClass = readThisClass();
            appendWithStyle(tp,"This class is: ",thisClass+"\n");
            String superClass = readSuperClass();
            appendWithStyle(tp,"Super class is: ",superClass+"\n");
            String interfaces = readInterfaces();
            appendWithStyle(tp,"Interfaces are: ",interfaces+"\n");

            readFieldsInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        appendToPane(tp,res,Color.green);
        frame.setSize(1200,600);
        tp.setCaretPosition(0);
        tp.setEditable(false);
        frame.setVisible(true);
    }

    public int readMagic() throws IOException {
        int magic = u4(dataInputStream);
        return magic;
    }

    public double readVersion() throws IOException {
        short minorVersion = u2(dataInputStream);
        short majorVersion = u2(dataInputStream);
        if (minorVersion==0x0003 && majorVersion == 0x002D) {
            return 45.3;
        } else if (minorVersion==0x0000) {
            double version = minorVersion << 16 | majorVersion;
            return version;
        } else {
            return -1;
        }
    }

    public void readConstantContents() throws IOException {
        int count = readConstantPoolsCount();
        appendWithStyle(tp,"Constant pools count is: ",new Integer(count).toString()+"\n");
        String[] pool_types = new String[count];
        String[] pool_values = new String[count];
        pool_specific_values = new String[count];
        for (int i=1;i<count;i++) {
            String[] result = readConstantContent();
            pool_types[i] = result[0];
            pool_values[i] = result[1];
        }
        for (int i=1;i<count;i++) {
            pool_specific_values[i] = getSpecificValue(pool_types,pool_values,i,count);
        }

        appendToPane(tp,"\n\n\n",Color.black);

        appendWithStyle(tp,fixedLengthString("Serial",15),
                fixedLengthString(" Pool type(s) ",35),
                fixedLengthString(" Pool value(s) ",50),
                fixedLengthString("             Specific pool value \n",20));

        appendToPane(tp,"\n",Color.black);

        for (int i=1;i<count;i++) {
            color=!color;
            if (!pool_values[i].equals(pool_specific_values[i])) {
                appendWithStyle(tp,fixedLengthString("# "+i+" = ",15),
                        fixedLengthString(" "+pool_types[i]+" ",35),
                        fixedLengthString(" "+pool_values[i]+" ",50),
                        fixedLengthString(" //\t"+pool_specific_values[i],20));
            }
            else
            {
                appendWithStyle(tp,fixedLengthString("# "+i+" = ",15),
                        fixedLengthString(" "+pool_types[i]+" ",35),
                        fixedLengthString(" "+pool_values[i]+" ",50),
                        "");
            }
            appendToPane(tp,"\n",Color.red);
        }

        appendToPane(tp,"\n\n\n",Color.black);
    }

    public String getSpecificValue(String[] pool_types,String[] pool_values,int idx, int count) {
        if (idx<0||idx>=count) {
            res+="invalid constant pool index";
            System.exit(0);
            return "";
        } else if (pool_types[idx].equals(ConstValues.ConstantPoolType.Class)) {
            int index = Integer.valueOf(pool_values[idx].substring(1));
            return getSpecificValue(pool_types,pool_values,index,count);
        } else if (pool_types[idx].equals(ConstValues.ConstantPoolType.Methodref)) {
            String[] values = pool_values[idx].split("\\.");
            int index1 = Integer.valueOf(values[0].substring(1));
            int index2 = Integer.valueOf(values[1].substring(1));
            return getSpecificValue(pool_types,pool_values,index1,count) + "." +
                    getSpecificValue(pool_types,pool_values,index2,count);
        } else if (pool_types[idx].equals(ConstValues.ConstantPoolType.NameAndType)) {
            String[] values = pool_values[idx].split(":");
            int index1 = Integer.valueOf(values[0].substring(1));
            int index2 = Integer.valueOf(values[1].substring(1));
            return getSpecificValue(pool_types,pool_values,index1,count) + ":" +
                    getSpecificValue(pool_types,pool_values,index2,count);
        } else {
            return pool_values[idx];
        }
    }

    public String[] readConstantContent() throws IOException {
        byte tag = u1(dataInputStream);
        String[] result = new String[2];
        String type = "", value = "";
        switch (tag) {
            case ConstValues.ConstantPool.CONSTANT_Utf8_info:
                int length = u2(dataInputStream);
                byte[] data = new byte[length];
                dataInputStream.read(data);
                type = ConstValues.ConstantPoolType.Utf8;
                value = new String(data);
                break;
            case ConstValues.ConstantPool.Constant_Integer_info:
                int int_value = u4(dataInputStream);
                type = ConstValues.ConstantPoolType.Utf8;
                value = int_value+"";
                break;
            case ConstValues.ConstantPool.Constant_Float_info:
                float float_value = u4(dataInputStream);
                type = ConstValues.ConstantPoolType.Utf8;
                value = float_value + "";
                break;
            case ConstValues.ConstantPool.Constant_Long_info:
                long long_value = u8(dataInputStream);
                type = ConstValues.ConstantPoolType.Utf8;
                value = long_value + "";
                break;
            case ConstValues.ConstantPool.Constant_Double_info:
                double double_value = u8(dataInputStream);
                type = ConstValues.ConstantPoolType.Utf8;
                value = double_value + "";
                break;
            case ConstValues.ConstantPool.Constant_Class_info:
                short class_index = u2(dataInputStream);
                type = ConstValues.ConstantPoolType.Class;
                value = "#"+class_index;
                break;
            case ConstValues.ConstantPool.Constant_String_info:
                short string_index = u2(dataInputStream);
                type = ConstValues.ConstantPoolType.Utf8;
                value = "#"+string_index;
                break;
            case ConstValues.ConstantPool.Constant_Fieldref_info:
                short filed_class_index = u2(dataInputStream);
                short field_nameAndType_index = u2(dataInputStream);
                type = ConstValues.ConstantPoolType.Fieldref;
                value = "#"+filed_class_index+"."+"#"+field_nameAndType_index;
                break;
            case ConstValues.ConstantPool.Constant_Methodref_info:
                short method_class_index = u2(dataInputStream);
                short method_nameAndType_index = u2(dataInputStream);
                type = ConstValues.ConstantPoolType.Methodref;
                value = "#"+method_class_index+"."+"#"+method_nameAndType_index;
                break;
            case ConstValues.ConstantPool.Constant_InterfaceMethodref_info:
                short interface_method_class_index = u2(dataInputStream);
                short interface_method_nameAndType_index = u2(dataInputStream);
                type = "InterfaceMethodref";
                value = "#"+interface_method_class_index+":"+"#"+interface_method_nameAndType_index;
                break;
            case ConstValues.ConstantPool.Constant_NameAndType_info:
                short name_index = u2(dataInputStream);
                short type_index = u2(dataInputStream);
                type = ConstValues.ConstantPoolType.NameAndType;
                value = "#"+name_index+":"+"#"+type_index;
                break;
            case ConstValues.ConstantPool.Constant_MethodHandle_info:
                byte reference_kind = u1(dataInputStream);
                short reference_index = u2(dataInputStream);
                break;
            case ConstValues.ConstantPool.Constant_MethodType_info:
                short description_index = u2(dataInputStream);
                break;
            case ConstValues.ConstantPool.Constant_InvokeDynamic_info:
                short bootstrap_method_attr_index = u2(dataInputStream);
                short name_and_type_index = u2(dataInputStream);
                break;
        }
        result[0] = type;
        result[1] = value;
        return result;
    }

    public short readConstantPoolsCount() throws IOException {
        short count = u2(dataInputStream);
        return count;
    }

    public String readAccessFlag() throws IOException {
        short accessFlag = u2(dataInputStream);
        int size = ConstValues.AccessFlag.AccessFlags.length;
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<size;i++) {
            if ((accessFlag&ConstValues.AccessFlag.AccessFlags[i])!=0) {
                builder.append(ConstValues.AccessFlag.AccessName[i]);
                if (i!=size-1) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    public String readThisClass() throws IOException {
        short index = u2(dataInputStream);
        return pool_specific_values[index];
    }

    public String readSuperClass() throws IOException {
        short index = u2(dataInputStream);
        return pool_specific_values[index];
    }

    public String readInterfaces() throws IOException {
        short count = u2(dataInputStream);
        StringBuilder builder = new StringBuilder();
        for (short i=0;i<count;i++) {
            int index = u2(dataInputStream);
            builder.append(pool_specific_values[index]);
            if (i!=count-1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public void readFieldsInfo() throws IOException {

        appendToPane(tp,"\nField info:\n",Color.BLUE);

        short field_count = u2(dataInputStream);
        for (int i=0;i<field_count;i++) {
            appendToPane(tp,readFieldInfo()+"\n",Color.BLUE);
        }
        tp.setCaretPosition(0);
    }

    public String readFieldInfo() throws IOException {
        String accessFlag = readFieldAccessFlag();
        short name_index = u2(dataInputStream);
        String name = pool_specific_values[name_index];
        short descriptor_index = u2(dataInputStream);
        String descriptor = pool_specific_values[descriptor_index];
        short attribute_count = u2(dataInputStream);
        return accessFlag+" "+name+" "+descriptor;
    }

    private String readFieldAccessFlag() throws IOException {
        short accessFlag = u2(dataInputStream);
        StringBuilder builder = new StringBuilder();
        int size = ConstValues.FieldAccessFlag.AccessFlags.length;
        for (int i=0;i<size;i++) {
            if ((accessFlag&ConstValues.FieldAccessFlag.AccessFlags[i])!=0) {
                builder.append(ConstValues.FieldAccessFlag.AccessName[i]);
                if (i!=size-1) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    public static byte u1(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readByte();
    }

    public static short u2(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readShort();
    }

    public static int u4(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readInt();
    }

    public static long u8(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readLong();
    }
}
