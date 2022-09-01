import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


class Amount
{
    // Values in Percentage
    static double discount=5.0;
    static double GST = 18.0;
    static  double getDiscount(double total)
    {
        try{
            return((total/100)*discount);
        }
        catch (Exception e)
        {
            return 0.0;
        }

    }
    static double getTaxes(double total)
    {
//        Percentage of CGST GST ,etc Taxes
        return((total/100)*GST);
    }
}

class Layout extends JFrame
{
    static JFrame mainFrame = null;
    static String path ="E:\\Advance Java\\Cafe\\src\\Images\\";
    static String pathDB ="E:\\Advance Java\\Cafe\\src\\Database\\";
    static int heigth = 0;
    static int width = 0;
    static Billing_Panel billing_panel = null;
    static CategoryPanel categoryPanel = null;
    static ItemListPanel itemListPanel = null;
    static Database db =new Database();
    Layout()
    {
        mainFrame = this;
        setSize(1200,1000);
        heigth = getHeight();
        width = getWidth();
        Container c = getContentPane();
        c.setLayout(new BorderLayout(2,2));

        billing_panel = new Billing_Panel();
        categoryPanel = new CategoryPanel();
        itemListPanel = new ItemListPanel();

//        Add scroll bars
        int v = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        int h = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        JScrollPane sp_category = new JScrollPane(categoryPanel,v,h);
        c.add(new Title_Panel(),BorderLayout.NORTH);
        c.add(sp_category,BorderLayout.EAST);
        c.add(itemListPanel,BorderLayout.CENTER);
        c.add(billing_panel,BorderLayout.WEST);
        c.add(new ExtraOptionPanel(),BorderLayout.SOUTH);
        setVisible(true);






    }

    public static void main(String arg[])
    {
        Layout l =new Layout();
        //System.out.println("Working Directory = " +
          //      System.getProperty("user.dir"));
        //System.err.println(Paths.get("").toAbsolutePath().toString());
    }
}
class Title_Panel extends JPanel
{
    Title_Panel()
    {
        JLabel title = new JLabel("Cafe Billing");
        title.setFont(new Font("Arial",Font.BOLD,25));


        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(Layout.width,50));
        add(title);
        // setLayout(new BoxLayout());
        setVisible(true);
        setBackground(Color.cyan);

    }
}
class Billing_Panel extends JPanel
{
    static int width = 400;
    JPanel billPanel;
    double Total=0.0;
    double overAllTotal = 0.0;
    JLabel discountL,taxL,totalL;
    int gridRow = 10;
    ArrayList<BillItem> billItemsArr = new ArrayList<BillItem>();
    Font bold = new Font("Arial",Font.BOLD,20);
    Billing_Panel()
    {
        setVisible(true);
        setBackground(Color.red);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width,500));
        JButton j = new  JButton("Counter Name");
        j.setPreferredSize(new Dimension(width-2,60));
        add(j,BorderLayout.NORTH);

        add(getBillPanel(),BorderLayout.CENTER);
        add(getPricePanel(),BorderLayout.SOUTH);

        update();



    }
    JScrollPane getBillPanel()
    {

        billPanel = new JPanel();
        billPanel.setLayout(new GridLayout(gridRow,1,5,5));
        billPanel.setBackground(new Color(255, 61, 0));

        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int v= ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
        JScrollPane jp = new JScrollPane(billPanel,v,h);
        return jp;

    }
    void addProduct(Product p)
    {
        int f=0;
//        Search of item - if exsist increase quantity
        for(BillItem b:billItemsArr)
        {
            if(b.product.itemName.equals(p.itemName))
            {

                        b.increaseQuantity();
                        Total = Total+p.price;

                f=1;
                break;
            }
        }
//        If new Product -> add to list
        if(f==0)
        {
            if(billItemsArr.size() >= gridRow)
            {
                gridRow++;
                billPanel.setLayout(new GridLayout(gridRow,1,5,5));
            }

            BillItem b = new BillItem(p);
            billItemsArr.add(b);
            billPanel.add(b);
            billPanel.updateUI();
            Total = Total+b.price;

        }


        update();
    }
    void removeProduct(String name)
    {
        for(BillItem b:billItemsArr)
        {
            if(b.product.itemName.equals(name))
            {
                billPanel.remove(b);
                Total = Total - (b.price);
                b.product.quantity = 1;
                billItemsArr.remove(b);
                if(billItemsArr.size()>=10)
                {
                    gridRow--;
                    billPanel.setLayout(new GridLayout(gridRow,1,5,5));
                }
                break;
            }
        }
        updateUI();
        update();
    }
    JPanel getPricePanel()
    {
        JPanel panel = new JPanel();
//        System.out.println("Height"+Layout.heigth);
        panel.setPreferredSize(new Dimension(width-4,Layout.heigth-900));
        panel.setBackground(new Color(1,141,230));
        panel.setForeground(Color.white);
        panel.setLayout(new GridLayout(3,2));

        JLabel discount = new JLabel("Discount:");  discount.setFont(bold);
        discountL = new JLabel(""+Amount.discount); discountL.setFont(bold);
        discountL.setHorizontalAlignment(JLabel.RIGHT);

        JLabel tax = new JLabel("Tax:"); tax.setFont(bold);
        taxL = new JLabel(""+Amount.getTaxes(Total)); taxL.setFont(bold);
        taxL.setHorizontalAlignment(JLabel.RIGHT);

        JLabel total = new JLabel("Total"); total.setFont(bold);
        totalL = new JLabel(""+Total); totalL.setFont(bold);
        totalL.setHorizontalAlignment(JLabel.RIGHT);

        panel.add(discount);
        panel.add(discountL);
        panel.add(tax);
        panel.add(taxL);
        panel.add(total);
        panel.add(totalL);



        return panel;
    }
    void update()
    {

        overAllTotal = (Total+Amount.getTaxes(Total))-Amount.getDiscount(Total);
        discountL.setText("Rs  "+Amount.getDiscount(Total)+" /-");
        taxL.setText(String.format("Rs  %.2f /-",Amount.getTaxes(Total)));
        totalL.setText("Rs "+(int)overAllTotal+" /- ");
    }

    void makeBill(String custName)
    {
        try {

            File file = new File("Customer.txt");
            file.createNewFile();

            FileOutputStream fout = new FileOutputStream(file);

            String s = "Name :\t"+custName+"\n";
            fout.write(s.getBytes());
            for(BillItem billItem:billItemsArr)
            {
                    s = billItem.product.itemName+"\t";
                    s+=billItem.product.quantity+"\t";
                    s+=billItem.product.price+"\n";
                    System.out.println(s);
                    fout.write(s.getBytes());
            }
            s =  "Total:\t"+totalL.getText()+"\n";
            fout.write(s.getBytes());
            s = "Discount:\t"+discountL.getText()+"\n";
            fout.write(s.getBytes());
            s = "Taxes:\t"+taxL.getText()+"\n";
            fout.write(s.getBytes());

            fout.close();
        }catch (Exception e)
        {
            System.out.println(e);
        }
        Total = 0 ;
        billItemsArr = new ArrayList<BillItem>();
        billPanel.removeAll();
        updateUI();
        update();
    }
}
class Product
{
    int id;
    String imgSrc = "";
    String itemName="";
    double price=0.0;
    int quantity=1;
    String category="none";
    Product() { }
    Product(String itemName,double price,String category)
    {
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }
    Product(int id,String itemName,double price,String category)
    {
        this.id =id;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }
    Product(String itemName,double price,int quantity)
    {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
    Product(String itemName,double price)
    {
        this.itemName = itemName;
        this.price = price;
    }

}
class BillItem extends JPanel implements ActionListener
{
    int width = Billing_Panel.width-35;
    int heigth = 60;
    Product product;
    JDialog dj;
    JTextField text;

    JLabel nameL,priceL,quantityL;
    double price;
    Font bold = new Font("Arial",Font.BOLD,20);
    Font labels = new Font("Arial",Font.BOLD,15);
    BillItem(Product product)
    {
        this.product = product;
        price = this.product.price*this.product.quantity;
        this.setPreferredSize(new Dimension(width,heigth));
        this.setBackground(Color.green);

        create();
        update();
    }
    void update()
    {

        quantityL.setText("Quantity:    "+(product.quantity)+"      ");
        priceL.setText("Price:    Rs "+(price)+"/-");
    }
    void setQuantity(int q)
    {
        product.quantity = q;
        price =  product.price * product.quantity;
        update();
    }
    void increaseQuantity()
    {
        product.quantity++;
        price =  product.price * product.quantity;
        update();
    }
    void create()
    {
        this.setLayout(new BorderLayout());
//        Item Details Panel
        JPanel  detailsPanel = new JPanel();
        detailsPanel.setPreferredSize(new Dimension(width-20,heigth));
        detailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        detailsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Dailog Box
                dj = new JDialog(Layout.mainFrame,"Set Quantity",true);
                dj.setLayout(new FlowLayout());
                dj.setSize(300,200);
                text = new JTextField(20);
                JButton setBtn = new JButton("Set Quantity");
                setBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try
                        {
                            int i= Integer.parseInt(text.getText());
                            if(i>0 && i<=100)
                            {

                                Product p = new Product(product.itemName,product.price);
                                p.quantity = i;
                                Layout.billing_panel.removeProduct(product.itemName);
                                Layout.billing_panel.addProduct(p);
                            }
                            else
                            {
                                text.setText("Must be in 1-100 range");
                            }
                        }
                        catch (Exception ex)
                        {
                            text.setText("Invalid Value");
                        }
                    }
                });
                dj.add(text);
                dj.add(setBtn);
                dj.setVisible(true);
            }
        });





        // Name
        nameL = new JLabel(product.itemName);
        nameL.setPreferredSize(new Dimension(width-60,20));
        nameL.setForeground(Color.red);
        nameL.setFont(bold);
        nameL.setBackground(Color.green);
        detailsPanel.add(nameL);
        // Quatity
        quantityL = new JLabel("Quantity:    "+(product.quantity)+"      ");
        quantityL.setFont(labels);
        detailsPanel.add(quantityL);

        // Price (Unit Price * Quantity)
        priceL = new JLabel("Price:    Rs "+(product.price)+"/-");
        priceL.setFont(labels);
        detailsPanel.add(priceL);


//        Delete btn
        JButton removeBtn = new JButton("X");
        removeBtn.addActionListener(this);
        add(removeBtn,BorderLayout.EAST);
        add(detailsPanel,BorderLayout.WEST);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("X"))
            Layout.billing_panel.removeProduct(product.itemName);

    }
}
class ItemListPanel extends JPanel
{
    JTextField searchField ;
    ArrayList<ItemBox> itemsArr = new ArrayList<ItemBox>();
    JPanel list = new JPanel();
    ItemListPanel()
    {
        setBackground(Color.red);
        setLayout(new BorderLayout());
        searchBox();
        // Item List Boxes Panel
        list.setLayout(new FlowLayout(FlowLayout.LEFT));
        list.setBackground(new Color(1,141,230));
        list.setPreferredSize(new Dimension(100,1000));
        JScrollPane sp_list = new JScrollPane(list);
        add(sp_list,BorderLayout.CENTER);
        loadProduct();

    }
    void loadProduct()
    {
        list.removeAll();
        ArrayList<Product> arr = new ArrayList();
        arr = Layout.db.getAllItems();
        for(Product p : arr)
        {
            addItemToList(p.itemName+".jpg",p);
        }
        list.updateUI();
    }
    void searchBox()
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
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                       System.out.println("insert Update");
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                System.out.println("remove Update");
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
            private void update()
            {
                JTextField t = searchField;
                // Display All
//                System.out.println("Changed");
//                System.out.println(t.getText());
                if(t.getText().equals(""))
                {
                    list.removeAll();
                    for(ItemBox i : itemsArr)
                    {
                        list.add(i);
                    }
                }
                else
                {
                    boolean found = false;
                    list.removeAll();
                    for(ItemBox i : itemsArr)
                    {
                        String a = i.product.itemName.toUpperCase();

                        if(a.startsWith(t.getText().toUpperCase()))
                        {
                            found = true;
                            list.add(i);
                        }
                    }
                    if(found==false)
                    {

                        JLabel j = new JLabel("No Items Found \'"+t.getText()+"\'");
                        j.setFont(new Font("Arial",Font.BOLD,20));
                        list.add(j);
                    }
                }


                list.updateUI();
            }

        });

        panel.add(j);
        panel.add(searchField);
        add(panel,BorderLayout.NORTH);
    }
    void addItemToList(String imgSrc,Product product)
    {
        if(itemsArr.size()>=24)
            list.setPreferredSize(new Dimension(200,1000+100));
        ItemBox i;
        if(imgSrc==null)
            i = new ItemBox(product);
        else
            i = new ItemBox(imgSrc,product);
        list.add(i);
        itemsArr.add(i);
    }

}
class ItemBox extends JPanel
{
    String imgSrc=Layout.path+"product.png";
    Product product = new Product();
    int itemBoxWidth = 200;
    int itemBoxHeigth = 200;
    JDialog dj;
    JTextField text;
    Font bold = new Font("Arial",Font.BOLD,17);
    ItemBox(Product product)
    {
        this.product = product;
        createItemBox();
    }
    ItemBox(String imgSrc,Product product)
    {
        this.imgSrc = Layout.path+imgSrc;
        // Check file image exist
        File folder = new File(Layout.path);
        int f=0;
        String[] files = folder.list();
        for (String file : files)
        {
            if(file.equals(imgSrc)) {
             f=1;
                break;
            }
        }
        if(f==0) this.imgSrc = Layout.path+"product.png";
        this.product = product;
        createItemBox();
    }
    ItemBox(String imgSrc,String itemName,double price){
        this.imgSrc = imgSrc;
        product.itemName = itemName;
        product.price = price;

        createItemBox();
//        Layout.billing_panel.addProduct(new Product(itemName,price,1));
    }
    ItemBox(String imgSrc,String itemName,double price,String category)
    {
        this.imgSrc = imgSrc;
        product.itemName = itemName;
        product.price = price;
        product.category = category;
        createItemBox();
    }
    ItemBox()
    {
        createItemBox();
    }
    ItemBox(String name ,double price)
    {
        product.itemName = name;
        product.price = price;
        createItemBox();
    }
    // [Image > itemName > Price]
    void createItemBox()
    {
        // Dailog Box
        dj = new JDialog(Layout.mainFrame,"Set Quantity",true);
        dj.setLayout(new FlowLayout());
        dj.setSize(300,200);
        text = new JTextField(20);
        JButton setBtn = new JButton("Set Quantity");
        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    try
                    {
                        int i= Integer.parseInt(text.getText());
                        if(i>0 && i<=100)
                        {

                            Product p = new Product(product.itemName,product.price);
                            p.quantity = i;
                            Layout.billing_panel.removeProduct(product.itemName);
                            Layout.billing_panel.addProduct(p);
                        }
                        else
                        {
                            text.setText("Must be in 1-100 range");
                        }
                    }
                    catch (Exception ex)
                    {
                        text.setText("Invalid Value");
                    }
            }
        });
        dj.add(text);
        dj.add(setBtn);


        this.setPreferredSize(new Dimension(itemBoxWidth,itemBoxHeigth));
//        this.setBackground(new Color(255,179,0));
        // Event
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getButton());
                if(e.getButton()==MouseEvent.BUTTON3)
                {
                    dj.setVisible(true);
                }
                else {
                    Layout.billing_panel.addProduct(product);
                }
            }
        });

//        Image
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(itemBoxWidth,(itemBoxHeigth-20)));

        ImageIcon i = new ImageIcon(imgSrc);
        Image ig = i.getImage();


//        System.out.println(imagePanel.getWidth()+" "+imagePanel.getHeight());
        Image i2 = ig.getScaledInstance(itemBoxWidth-20,130, java.awt.Image.SCALE_AREA_AVERAGING);
        i = new ImageIcon(i2);

        JLabel image =new JLabel(i);

        this.add(image);
//        Item Name
        JPanel namePanel = new JPanel();
        namePanel.setBackground(new Color(255,179,0));
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        namePanel.setPreferredSize(new Dimension(itemBoxWidth,24));
        JLabel name = new JLabel(product.itemName);
        name.setFont(bold);
        namePanel.add(name);
        this.add(namePanel);
//        Price
        JPanel pricePanel = new JPanel();
        pricePanel.setBackground(new Color(0, 200, 83));
        pricePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pricePanel.setPreferredSize(new Dimension(itemBoxWidth,23));
        JLabel p = new JLabel("Rs "+product.price+" /-");
        p.setFont(bold);
        pricePanel.add(p);
        this.add(pricePanel);

    }
}
class CategoryPanel extends JPanel
{   static int width = 200;
//    String imgSrc = Layout.path+"category2.png";
    CategoryPanel()
    {
        setBackground(new Color(255, 61, 0));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        setPreferredSize(new Dimension(width,Layout.heigth));
        add(new CategoryItem("All"));
        add(new CategoryItem("Burger"));
        add(new CategoryItem("Pizza"));
        add(new CategoryItem("Coffee"));
    }

}
class CategoryItem extends JPanel
{
    String imgSrc = Layout.path+"category3.png";
    String cateName;
    CategoryItem(String name)
    {
        cateName=name;
        setPreferredSize(new Dimension(CategoryPanel.width-10,200));
        create();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CategoryItem c = (CategoryItem) e.getSource();
                if(c.cateName.equals("All"))
                {
                    Layout.itemListPanel.list.removeAll();
                    for(ItemBox i:Layout.itemListPanel.itemsArr)
                    {
                        Layout.itemListPanel.list.add(i);
                    }
                }
                else
                {
                    Layout.itemListPanel.list.removeAll();
                    for(ItemBox i:Layout.itemListPanel.itemsArr)
                    {
                        if(i.product.category.equals(c.cateName))
                            Layout.itemListPanel.list.add(i);
                    }
                }
                Layout.itemListPanel.list.updateUI();
            }
        });


    }
    void create()
    {
        // Image
        ImageIcon i = new ImageIcon(imgSrc);
        Image ig = i.getImage();
        Image i2 = ig.getScaledInstance(CategoryPanel.width-20,155, java.awt.Image.SCALE_AREA_AVERAGING);
        i = new ImageIcon(i2);
        JLabel image =new JLabel(i);

        this.add(image);
        this.setBackground(Color.green);
        // Category Name Label
        JLabel cname = new JLabel(cateName);
        cname.setFont(new Font("Arail",Font.BOLD,15));
        this.add(cname);
    }


}
class ExtraOptionPanel extends JPanel implements ActionListener
{
    private JDialog dj;
    JTextField text;

    ExtraOptionPanel()
    {
        setPreferredSize(new Dimension(Layout.width,70));
        setBackground(Color.cyan);
        JButton p = new JButton("Manage Products");
        p.addActionListener(this);
        add(p);
        JButton p1 = new JButton("Refresh");
        p1.addActionListener(this);
        add(p1);
        p1 = new JButton("Show Stats");
        p1.addActionListener(this);
        add(p1);
        p1 = new JButton("Make Bill");
        p1.addActionListener(this);
        add(p1);


    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand())
        {
            case "Manage Products": new ProductFrame(); break;
            case "Refresh" :  Layout.itemListPanel.loadProduct(); break;
            case "Make Bill" : printBill(); break;
            case "Show Stats" : new StatFrame().setVisible(true); break;
        }
    }
    void  printBill()
    {


        dj = new JDialog(Layout.mainFrame,"Customer Info",true);
        dj.setLayout(new FlowLayout());
        dj.setSize(300,200);
        text = new JTextField(20);
        JButton billBtn = new JButton("Make Bill");
        billBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Layout.db.insertCustomer(text.getText(),Layout.billing_panel.Total);
                Layout.billing_panel.makeBill(text.getText());
                dj.setVisible(false);

                JDialog dialog = new JDialog(Layout.mainFrame,true);
                JLabel label = new JLabel("Collect your Bill...");
                label.setFont(new Font("Arial",Font.BOLD,25));
                dialog.setLayout(new FlowLayout());
                dialog.add(label);
                dialog.setSize(300,100);
                dialog.setVisible(true);

            }
        });
        dj.add(text);
        dj.add(billBtn);
        dj.setVisible(true);




    }
}

class StatFrame extends JFrame
{
    Font font = new Font("Arial",Font.BOLD,20);
    String dateStr;
    int[] custDetaails;
    StatFrame()
    {
        setTitle("Todays Sells");
        Container c = getContentPane();
        setSize(500,300);
        c.setLayout(null);
        setBackground(new Color(50, 125, 255));
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateStr = formatter.format(date);

        custDetaails = Layout.db.getSellDetails(dateStr);


        JLabel dateL = new JLabel("Date: "+dateStr);
        dateL.setFont(font);
        dateL.setBounds(160,20,200,25);

        JLabel custL = new JLabel("Visited Customer: "+custDetaails[0]);
        custL.setFont(font);
        custL.setBounds(100,80,200,30);

        JLabel totalL = new JLabel("Todays Sell: "+custDetaails[1]+" /-");
        totalL.setFont(font);
        totalL.setBounds(100,120,200,30);

        LocalDate date_ = LocalDate.now().minusDays(1);
        date = Date.from(date_.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        dateStr = formatter.format(date);
        int pev_total = Layout.db.getSellDetails(dateStr)[1];



        JLabel statusL = new JLabel("Status : ");
        statusL.setFont(font);
        statusL.setBounds(100,160,300,30);

        int total = custDetaails[1] - pev_total;

        if(total >= 0)
        {
            statusL.setForeground(Color.green);
            statusL.setText("Status:  Profit  "+total);
        }
        else
        {
            statusL.setForeground(Color.red);
            statusL.setText("Status:   Loss   "+total);
        }

        c.add(dateL);
        c.add(custL);
        c.add(totalL);
        c.add(statusL);

    }


}