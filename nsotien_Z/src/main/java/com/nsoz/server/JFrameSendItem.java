package com.nsoz.server;

import com.nsoz.constants.ItemName;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.model.Char;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author trant
 */
public class JFrameSendItem extends JFrame{
    
    private javax.swing.JTextField idItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField luong;
    private javax.swing.JTextField name;
    private javax.swing.JTextField quantity;
    private javax.swing.JButton xacnhan;
    private javax.swing.JTextField xu;
    private javax.swing.JTextField yen;
    private javax.swing.JTextField lock;
    
    
    public JFrameSendItem() {
        initComponents();
    }
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        quantity = new javax.swing.JTextField();
        xu = new javax.swing.JTextField();
        luong = new javax.swing.JTextField();
        yen = new javax.swing.JTextField();
        name = new javax.swing.JTextField();
        idItem = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        xacnhan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lock = new javax.swing.JTextField();
        
        
        idItem.setText("0");
        quantity.setText("0");
        lock.setText("true");
        xu.setText("0");
        yen.setText("0");
        luong.setText("0");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Send Item SERVER QUANGCHEN");

        jLabel1.setText("Tên");

        jLabel2.setText("ID Item");

        jLabel3.setText("Số lượng");

        jLabel4.setText("Yên");

        jLabel5.setText("Xu");

        jLabel6.setText("Lượng");
        
        jLabel8.setText("lock");

        jLabel7.setText("QUANGCHEN");
        jLabel7.setToolTipText("");

        xacnhan.setText("Xác nhận");
        
        xacnhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xacnhanActionPerformed(evt);
            }

            private void xacnhanActionPerformed(ActionEvent evt) {
                if (name.getText().equals("") || idItem.getText().equals("") 
                        || quantity.getText().equals("") || yen.getText().equals("") 
                        || xu.getText().equals("") || luong.getText().equals("")|| lock.getText().equals("")) {
                    JOptionPane.showMessageDialog(rootPane, "Vui lòng nhập đủ các trường!");
                } else {
                    Char chars = Char.findCharByName(name.getText());
                   if (chars != null) {
                       if (!checkNumber(idItem.getText()) || !checkNumber(quantity.getText()) 
                               || !checkNumber(xu.getText()) || !checkNumber(yen.getText()) || !checkNumber(luong.getText())|| (!lock.getText().equals("true") && !lock.getText().equals("false"))) {
                           JOptionPane.showMessageDialog(rootPane, "Sai định dạng!");
                           return;
                       }
                        if (Integer.parseInt(idItem.getText()) > 0) {
                            Item item = ItemFactory.getInstance().newItem(Integer.parseInt(idItem.getText()));
                            if(lock.getText().equals("true")){
                            item.isLock = true;
                            }else{
                            item.isLock = false; 
                            }
                            item.setQuantity(Integer.parseInt(quantity.getText()));
                            chars.addItemToBag(item);
                        }
                        chars.addYen(Long.parseLong(yen.getText()));
                        chars.addGold(Integer.parseInt(luong.getText()));
                        chars.addCoin(Long.parseLong(xu.getText()));
                   } else {
                       JOptionPane.showMessageDialog(rootPane, "Người này không tồn tại hoặc không online!");
                   }
                }
                System.out.println("" + name.getText() + " id " + idItem.getText() + " solg " + quantity.getText() + " yen " + yen.getText() + " xu " + xu.getText() + " luong " + luong.getText());
                JOptionPane.showMessageDialog(rootPane, "" + name.getText() + " id " + idItem.getText() + " solg " + quantity.getText() + " yen " + yen.getText() + " xu " + xu.getText() + " luong " + luong.getText());
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(xacnhan)
                .addGap(110, 110, 110))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addComponent(luong, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel8)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(name, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(idItem)
                                    .addComponent(quantity)
                                    .addComponent(xu)
                                    .addComponent(lock)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(yen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(idItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(19, 19, 19)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(luong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(yen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(xacnhan)
                .addGap(16, 16, 16))
        );

        pack();
    }
    public static boolean checkNumber(String str) {
        if (str.matches("[0-9]+")) {
            return true;
        } else {
            return false;
        }
    }
    public static void run() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameSendItem().setVisible(true);
            }
        });
    }
}
