import  javax.swing.*;
import java.awt.*;
public class Test {

    public  static  void  main(String args[])
    {
        Database db  = new Database();

      
        StatFrame statFrame = new StatFrame();
        statFrame.setVisible(true);

        JDialog dialog = new JDialog(statFrame,true);


        
        JLabel label = new JLabel("Collect your Bill...");
        label.setFont(new Font("Arial",Font.BOLD,25));
        dialog.setLayout(new FlowLayout());
        dialog.add(label);
        dialog.setSize(300,100);
        dialog.setVisible(true);



    }

}
