
package test_java3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Test1 extends javax.swing.JFrame {
     Connection con; 
     DefaultTableModel tblModel;
     int index = 0;

    public Test1() {
        initComponents();
        setResizable(false); 
        setLocationRelativeTo(null); 
        con = MySqlConnection.getMySqlConnection("QuanlySach");
        setCols(); 
        loadDataToTable(); 
        showDetail();
    }

    private void setCols() {
        tblModel = (DefaultTableModel) tblSach.getModel();
        String[] cols = {"MÃ SÁCH", "TÊN SÁCH", "GIÁ", "NĂM XB"};
        tblModel.setColumnIdentifiers(cols);
        tblSach.getColumn("MÃ SÁCH").setPreferredWidth(90);
        tblSach.getColumn("TÊN SÁCH").setPreferredWidth(210);
        tblSach.getColumn("GIÁ").setPreferredWidth(100);
        tblSach.getColumn("NĂM XB").setPreferredWidth(100);
    }

    private void loadDataToTable() {
        try {
            tblModel.setRowCount(0);
            if (con != null) {
                PreparedStatement st = con.prepareStatement("Select * from Sach"); 
                ResultSet rs = st.executeQuery(); 

                while (rs.next()) {
                    String maSach = rs.getString(1);
                    String tenSach = rs.getString(2);
                    double gia = rs.getDouble(3);
                    int namXB = rs.getInt(4);
                    Object[] rows = {maSach, tenSach, gia, namXB};
                    tblModel.addRow(rows);
                }
                st.close(); 
                rs.close(); 
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex);
            System.exit(0);
        }
    }
    
    private void showDetail() {
        txtMaSach.setText(tblSach.getValueAt(index, 0).toString());
        txtTenSach.setText(tblSach.getValueAt(index, 1).toString());
        txtGia.setText(tblSach.getValueAt(index, 2).toString());
        txtNamXB.setText(tblSach.getValueAt(index, 3).toString());
    }
    
    private void findIndex(String ma) {
        int idx = 0;
        Vector all = tblModel.getDataVector();
        while (idx < all.size()) {
            Vector s = (Vector) all.elementAt(idx);
            if (s.elementAt(0).toString().equals(ma)) {
                index = idx;
                break;
            }
            idx++;
        }
        
    }
    private void clearJTextField() {
        txtMaSach.setText("");
        txtTenSach.setText("");
        txtGia.setText("");
        txtNamXB.setText("");
    }
    public boolean checkEmpty(String txt, String hint) {
        if (txt.equals("")) {
            JOptionPane.showMessageDialog(null, hint, "Lỗi", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
    
    public boolean checkDuplicate(String txt, String tableName, String colName) {
        
        try {
            PreparedStatement st = con.prepareStatement("Select * From "+tableName +" Where "+ colName +" = ?;");
            st.setString(1, txt);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true; 
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex);
        }
        return false; 
    }
    
    private boolean checkBeforeSaving() {
        String maSach = txtMaSach.getText();
        String tenSach = txtTenSach.getText();
        String giaSach = txtGia.getText();
        String namXB = txtNamXB.getText();
        
        if(checkEmpty(maSach,"Mã sách chưa được nhập!")){
            return true;
        }
        if(checkEmpty(tenSach,"Tên sách chưa được nhập!")){
            return true;
        }
        if(checkEmpty(giaSach,"Giá sách chưa được nhập!")){
            return true;
        }
        if(checkEmpty(namXB,"Năm Xuất Bản sách chưa được nhập!")){
            return true;
        }
        if(Double.parseDouble(giaSach) < 0){
            JOptionPane.showMessageDialog(this, "GIÁ SÁCH phải lớn hơn hoặc bằng 0! "
                    + "Vui lòng nhập lại gía!");
            return true;
        }
        if(Integer.parseInt(namXB) < 1900){
            JOptionPane.showMessageDialog(this, "Năm Xuất Bản phải lớn hơn hoặc bằng năm 1900! "
                    + "Vui lòng nhập lại năm xuất bản!");
            return true;
        }
        
        if(checkDuplicate(maSach, "Sach", "masach")){
            JOptionPane.showMessageDialog(this, "MÃ SÁCH này đã tồn tại! "
                    + "Vui lòng nhập lại mã mới!");
            return true;
        }
        return false;
    }
    
    private void saveDataToSachTable() {
        
        String maSach = txtMaSach.getText();
        String tenSach = txtTenSach.getText();
        double giaSach = Double.parseDouble(txtGia.getText());
        int namXB = Integer.parseInt(txtNamXB.getText());
        
        try {
            String sqlInsert = "Insert into Sach values (?,?,?,?)";
            PreparedStatement st = con.prepareStatement(sqlInsert); // Biên dịch câu lệnh SQL trước
            st.setString(1, maSach);
            st.setString(2, tenSach);
            st.setDouble(3, giaSach);
            st.setInt(4, namXB);

            int rs = st.executeUpdate(); 
            if (rs > 0) {
                /* Cách 1: Dùng addRow() để thêm dữ liệu vào cuối bảng JTable trên form*/
                tblModel.addRow(new Object[]{maSach, tenSach, giaSach, namXB});
                
                /* Cách 2: Gọi loadDataToTable() để đọc tất cả dữ liệu từ bảng “Students” đổ lên JTable */
                 // loadDataToTable();
                
                findIndex(maSach);
                tblSach.setRowSelectionInterval(index, index); 
                JOptionPane.showMessageDialog(this, "Mã Sách " + txtMaSach.getText() + " đã được lưu thành công!");
            }
            st.close(); 
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi:" + ex);
            System.exit(0);
        }
    }
    
    private void filter() {
        try {
            tblModel.setRowCount(0);
            if (con != null) {
                PreparedStatement st = con.prepareStatement("Select * From Sach order by gia desc limit 2");
                ResultSet rs = st.executeQuery(); 
                while (rs.next()) {
                    String maSach = rs.getString(1);
                    String tenSach = rs.getString(2);
                    double gia = rs.getDouble(3);
                    int namXB = rs.getInt(4);
                    Object[] rows = {maSach, tenSach, gia, namXB};
                    tblModel.addRow(rows);
                }
                st.close(); 
                rs.close(); 
            }

        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex);
            System.exit(0);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNamXB = new javax.swing.JTextField();
        txtTenSach = new javax.swing.JTextField();
        txtMaSach = new javax.swing.JTextField();
        txtGia = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSach = new javax.swing.JTable();
        btnNew = new javax.swing.JButton();
        btnFilter = new javax.swing.JButton();
        btnInsert = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUẢN LÝ SÁCH");

        jLabel2.setText("Mã sách:");

        jLabel3.setText("Tên sách:");

        jLabel4.setText("Giá sách:");

        jLabel5.setText("Năm xuất bản:");

        tblSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSachMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblSach);

        btnNew.setText("New");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMaSach, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTenSach, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(txtNamXB, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInsert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFilter)
                .addGap(100, 100, 100))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaSach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtTenSach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNamXB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew)
                    .addComponent(btnInsert)
                    .addComponent(btnFilter))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblSachMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSachMouseReleased
        index = tblSach.getSelectedRow();
        this.showDetail();
    }//GEN-LAST:event_tblSachMouseReleased

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        this.clearJTextField();
        loadDataToTable();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        loadDataToTable();
        if(this.checkBeforeSaving()){
            return;
        }
        this.saveDataToSachTable();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        this.filter();
        index = 0;
        tblSach.setRowSelectionInterval(index, index); 
        showDetail();
    }//GEN-LAST:event_btnFilterActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnNew;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblSach;
    private javax.swing.JTextField txtGia;
    private javax.swing.JTextField txtMaSach;
    private javax.swing.JTextField txtNamXB;
    private javax.swing.JTextField txtTenSach;
    // End of variables declaration//GEN-END:variables


}
