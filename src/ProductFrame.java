import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProductFrame extends JFrame implements ActionListener
{

    static JFrame mainFrame;
    JButton saveBtn,addBtn,removeBtn,editBtn,newBtn;
    static ProductDetailPanel productDetailPanel;
    static ProductPanel productPanel;
    ProductFrame()
    {
        mainFrame = this;
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        productDetailPanel = new ProductDetailPanel();
        productPanel = new ProductPanel();
        JScrollPane jsp = new JScrollPane(productPanel);

        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout(FlowLayout.RIGHT));
        saveBtn = new JButton("Save");
        addBtn = new JButton("Add");
        removeBtn = new JButton("Remove");
        editBtn = new JButton("Edit");
        newBtn = new JButton("New");

        saveBtn.addActionListener(this);
        addBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        editBtn.addActionListener(this);
        newBtn.addActionListener(this);
        controls.add(newBtn);
        controls.add(saveBtn);
        controls.add(addBtn);
        controls.add(removeBtn);
        controls.add(editBtn);

        c.add(controls,BorderLayout.NORTH);
        c.add(jsp,BorderLayout.WEST);
        c.add(productDetailPanel, BorderLayout.CENTER);


        setSize(1200,1000);
        setVisible(true);


    }
    public static void main(String[] args)
    {
        new ProductFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand())
        {
            case "Save" : productDetailPanel.save(); break;
            case "Edit" : productDetailPanel.edit(); break;
            case "Remove" : productDetailPanel.removeProduct(); break;
            case "Add" : productDetailPanel.addProduct(); break;
            case "New" : productDetailPanel.show(new Product());
        }
    }
}
class ProductPanel extends JPanel
{
    JTextField searchField;
    JPanel listPanel;
    Database db;
    ArrayList<Product> productsArr = new ArrayList<Product>();
    ProductPanel()
    {
        db =new Database();
        setLayout(new BorderLayout());
        setBackground(Color.red);
        setPreferredSize(new Dimension(500,1000));
//        createSearchBox();
        createListPanel();

    }
    void createSearchBox()
    {

        JPanel panel = new JPanel();
        panel.setBackground(new Color(12,34,56));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(getWidth(),50));

        Font f = new Font("Arial",Font.BOLD,20);

        JLabel j = new JLabel("Search Item:");
        j.setFont(f);
        j.setForeground(Color.white);
        // Search Field and Event *
        searchField = new JTextField(20);
        searchField.setFont(f);
//        searchField.getDocument().addDocumentListener(new DocumentListener() {

        panel.add(j);
        panel.add(searchField);
        add(panel,BorderLayout.NORTH);
    }
    void createListPanel()
    {
        listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(10,1));
        listPanel.setBackground(Color.yellow);
        // Load Products from Database

        loadProducts();
        add(listPanel,BorderLayout.CENTER);

    }
    void loadProducts()
    {
        listPanel.removeAll();
        try {
            productsArr = db.getAllItems();
        }catch (Exception e){
            e.printStackTrace();
        }

        for(Product p:productsArr)
            addToList(p);

        listPanel.updateUI();
    }

    void addToList(Product p)
    {
        ProductButton pb = new ProductButton(p);
        listPanel.add(pb);
    }
    class ProductButton extends JButton implements ActionListener
    {
        Product product;
        ProductButton(Product product)
        {
            super(product.itemName);
            this.product = product;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e)
        {
            ProductFrame.productDetailPanel.show(product);
        }
    }
}
class ProductDetailPanel extends JPanel implements  ActionListener
{
    JTextField nameT,priceT,categoryT,imageURL;
    Product product;
    Database db;
    JButton uploadBtn;
    JLabel imageLabel;
    String filePath=null;
    ProductDetailPanel()
    {
        db = new Database();
        setBackground(Color.blue);
        this.product = new Product("",0.0,"none");
        show(this.product);
    }
    ImageIcon createImageIcon(String url,int w,int h)
    {
        ImageIcon i = new ImageIcon(url);
        Image ig = i.getImage();
        Image i2 = ig.getScaledInstance(w,h, java.awt.Image.SCALE_AREA_AVERAGING);
        i = new ImageIcon(i2);

        return i;
    }
    void show(Product product)
    {
        this.product = product;
        removeAll();
        updateUI();
        // Image
            // Check file image exist
        int f = 0;
        try {
            File folder = new File(Layout.path);

            String[] files = folder.list();
            for (String file : files) {
                if (file.equals(product.itemName + ".jpg")) {
                    product.imgSrc = Layout.path + product.itemName + ".jpg";
                    f = 1;
                    break;
                }
            }
        }catch (Exception e){}
        if(f==0) product.imgSrc = Layout.path+"product.png";


        imageLabel = new JLabel(createImageIcon(product.imgSrc,300,300));
        add(imageLabel);

        JPanel  detailsP = new JPanel();
        detailsP.setLayout(new GridLayout(4,3,10,10));
        add(detailsP);

        Font font = new Font("Arial",Font.PLAIN,18);
        JLabel nameL = new JLabel("Name:");
        nameL.setFont(font);
        nameT = new JTextField(product.itemName,20);
        nameT.setFont(font);
        nameT.setEditable(false);

        JLabel priceL = new JLabel("Price(Rs):");
        priceL.setFont(font);
        priceT = new JTextField(""+product.price,20);
        priceT.setFont(font);
        priceT.setEditable(false);

        JLabel categoryL = new JLabel("Category:");
        categoryL.setFont(font);
        categoryT = new JTextField(product.category,20);
        categoryT.setFont(font);
        categoryT.setEditable(false);

        uploadBtn = new JButton("Browse");
        uploadBtn.setFont(font);
        uploadBtn.setEnabled(false);
        uploadBtn.addActionListener(this);

        imageURL = new JTextField("URL",20);
        imageURL.setFont(font);
        imageURL.setEditable(false);





        detailsP.add(nameL);
        detailsP.add(nameT);
        detailsP.add(priceL);
        detailsP.add(priceT);
        detailsP.add(categoryL);
        detailsP.add(categoryT);
        detailsP.add(uploadBtn);
        detailsP.add(imageURL);

    }
    void edit()
    {
        nameT.setEditable(true);
        priceT.setEditable(true);
        categoryT.setEditable(true);
        uploadBtn.setEnabled(true);
        imageURL.setEditable(true);
    }
    void save()
    {
        product.itemName = nameT.getText();
        product.price = Double.parseDouble(priceT.getText());
        product.category = categoryT.getText();
        db.update(product);

    }
    void addProduct()
    {

        product.itemName = nameT.getText();
        product.price = Double.parseDouble(priceT.getText());
        product.category = categoryT.getText();
        db.add(product);
        ProductFrame.productPanel.loadProducts();
    }
    void removeProduct()
    {
            db.remove(product);
            ProductFrame.productPanel.loadProducts();
    }
    void copyToMyDir()
    {
        product.itemName = nameT.getText();
        try
        {
            FileInputStream f = new FileInputStream(filePath);
            File file = new File(Layout.path+product.itemName+".jpg");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            int i=0;
            while((i=f.read())!=-1)
            {
                out.write((byte)i);
            }
            f.close();
            out.close();
        }catch (Exception e)
        {
                e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Browse"))
        {
            FileDialog fd = new FileDialog(ProductFrame.mainFrame,"Select Image",FileDialog.LOAD);
            fd.setVisible(true);
            filePath = fd.getDirectory()+fd.getFile();
            System.out.println(filePath);
            imageURL.setText(filePath);
            imageLabel.setIcon(createImageIcon(filePath,300,300));
            copyToMyDir();
        }
    }
}
class Database {
    Connection conn;
    Statement st;
    Database()
    {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection("jdbc:ucanaccess://"+Layout.pathDB+"Cafe.accdb");
            st = conn.createStatement();
        }
        catch (Exception e){ e.printStackTrace(); }
    }
    ArrayList<Product> getAllItems()
    {
        ArrayList<Product> arr = new ArrayList<>();
        try {
            ResultSet res = st.executeQuery("Select * from Product");

            while (res.next()) {
                arr.add(new Product(res.getInt("ID"),res.getString("Name"),
                        res.getDouble("Price"),
                        res.getString("Category")));
            }


        }
        catch (Exception e){e.printStackTrace();}
        return  arr;
    }
    void add(Product p)
    {
        try {

            String s = "Insert into Product(Name,Price,Category) values('"+p.itemName+"',"+p.price+",'"+p.category+"')";
            System.out.println(s);
            int b = st.executeUpdate(s);
            System.out.println(b);
            if (b<=0)
                System.out.println("Error Inserting");
        }
        catch (Exception e){e.printStackTrace();}
    }
    void update(Product p)
    {
        try {

            String s = "Update Product set Name = '"+p.itemName+"' , Price = "+p.price+" , Category='"+p.category+"' where id="+p.id;
            System.out.println(s);
            int b = st.executeUpdate(s);
            if (b<=0)
                System.out.println("Error Inserting");
        }
        catch (Exception e){e.printStackTrace();}
    }
    void remove(Product p)
    {
        try {


            int b = st.executeUpdate("Delete from Product where id="+p.id);
            if (b<=0)
                System.out.println("Error Inserting");
        }
        catch (Exception e){e.printStackTrace();}
    }
    void insertCustomer(String name,double paid)
    {
        try
        {
            name = "'"+name+"'";
            java.util.Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateStr = "'"+formatter.format(date)+"'";
            String q = "Insert into Customer(Date,Name,Paid) values("+dateStr+","+name+","+paid+")";
            System.out.println(q);
            st.executeUpdate(q);

        }catch (Exception e)
        {
            System.out.println(e);
        }
    }
    int[] getSellDetails(String date)
    {
        int d[] = new int[2];
        try
        {
            String q = "Select COUNT(Name) from Customer where Date = '"+date+"';";
            ResultSet res = st.executeQuery(q);
            while (res.next()) {
               d[0] = res.getInt(1);
            }
            q = "Select SUM(Paid) from Customer where Date = '"+date+"';";
            res = st.executeQuery(q);
            while (res.next()) {
                d[1] = res.getInt(1);
            }

        System.out.println(d[0]+" "+d[1]);
        }catch (Exception e)
        {
            System.out.println(e);
        }

        return d;
    }

    @Override
    protected void finalize() throws Throwable {
        st.close();
    }
}

