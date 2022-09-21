package test_java3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Test2 extends javax.swing.JFrame {
    Connection con; 
    DefaultTableModel tblModel;
    int index = 0;
    
    public Test2() {
        initComponents();
        setResizable(false); 
        setLocationRelativeTo(null); 
        con = MySqlConnection.getMySqlConnection("QuanLyLuong");
        setCols(); 
        loadDataToTable(); 
        showDetail();
    }

    private void setCols() {
        tblModel = (DefaultTableModel) tblNhanVien.getModel();
        String[] cols = {"MÃ NV", "HỌ TÊN", "LƯƠNG", "THƯỞNG", "TỔNG LƯƠNG"};
        tblModel.setColumnIdentifiers(cols);
        tblNhanVien.getColumn("MÃ NV").setPreferredWidth(90);
        tblNhanVien.getColumn("HỌ TÊN").setPreferredWidth(210);
        tblNhanVien.getColumn("THƯỞNG").setPreferredWidth(100);
        tblNhanVien.getColumn("LƯƠNG").setPreferredWidth(100);
        tblNhanVien.getColumn("TỔNG LƯƠNG").setPreferredWidth(100);
    }

    private void loadDataToTable() {
        try {
            tblModel.setRowCount(0);
            if (con != null) {
                PreparedStatement st = con.prepareStatement("Select * from NhanVien"); 
                ResultSet rs = st.executeQuery(); 

                while (rs.next()) {
                    String maNV = rs.getString(1);
                    String hoTen = rs.getString(2);
                    int luong = rs.getInt(3);
                    int thuong = rs.getInt(4);
                    int tongLuong = luong + thuong;
                    Object[] rows = {maNV, hoTen, luong, thuong, tongLuong};
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
        txtMaNV.setText(tblNhanVien.getValueAt(index, 0).toString());
        txtHoTen.setText(tblNhanVien.getValueAt(index, 1).toString());
        txtLuong.setText(tblNhanVien.getValueAt(index, 2).toString());
        txtThuong.setText(tblNhanVien.getValueAt(index, 3).toString());
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
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtLuong.setText("");
        txtThuong.setText("");
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
        String maNV = txtMaNV.getText();
        String hoTen = txtHoTen.getText();
        String luong = txtLuong.getText();
        String thuong = txtThuong.getText();
        
        if(checkEmpty(maNV,"Mã Nhân Viên chưa được nhập!")){
            return true;
        }
        if(checkEmpty(hoTen,"Tên Nhân Viên chưa được nhập!")){
            return true;
        }
        if(checkEmpty(luong,"Lương Nhân Viên chưa được nhập!")){
            return true;
        }
        if(checkEmpty(thuong,"Thưởng Nhân Viên chưa được nhập!")){
            return true;
        }
        if(Integer.parseInt(luong) < 0){
            JOptionPane.showMessageDialog(this, "LƯƠNG phải lớn hơn hoặc bằng 0! "
                    + "Vui lòng nhập lại lương!");
            return true;
        }
        if(Integer.parseInt(thuong) < 0){
            JOptionPane.showMessageDialog(this, "THƯỞNG phải lớn hơn hoặc bằng 0! "
                    + "Vui lòng nhập lại thưởng!");
            return true;
        }
        
        if(checkDuplicate(maNV, "NhanVien", "maNV")){
            JOptionPane.showMessageDialog(this, "MÃ NHÂN VIÊN này đã tồn tại! "
                    + "Vui lòng nhập lại mã mới!");
            return true;
        }
        return false;
    }
    
    private void saveDataToNhanVienTable() {
        
        String maNV = txtMaNV.getText();
        String hoTen = txtHoTen.getText();
        int luong = Integer.parseInt(txtLuong.getText());
        int thuong = Integer.parseInt(txtThuong.getText());
        
        try {
            String sqlInsert = "Insert into NhanVien values (?,?,?,?)";
            PreparedStatement st = con.prepareStatement(sqlInsert); 
            st.setString(1, maNV);
            st.setString(2, hoTen);
            st.setInt(3, luong);
            st.setInt(4, thuong);

            int rs = st.executeUpdate(); 
            if (rs > 0) {
                /* Cách 1: Dùng addRow() để thêm dữ liệu vào cuối bảng JTable trên form*/
                tblModel.addRow(new Object[]{maNV, hoTen, luong, thuong, thuong+luong});
                
                /* Cách 2: Gọi loadDataToTable() để đọc tất cả dữ liệu từ bảng “Students” đổ lên JTable */
                 // loadDataToTable();
                
                findIndex(maNV);
                tblNhanVien.setRowSelectionInterval(index, index); 
                JOptionPane.showMessageDialog(this, "Mã Nhân Viên " + txtMaNV.getText() + " đã được lưu thành công!");
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
                String sql = "Select *, (luong + thuong) From NhanVien "
                                + "where (luong + thuong) > 500 "
                                + "order by (luong + thuong) desc limit 2;";
                
                PreparedStatement st = con.prepareStatement(sql);
                ResultSet rs = st.executeQuery(); 
                while (rs.next()) {
                    String maNV = rs.getString(1);
                    String hoTen = rs.getString(2);
                    int luong = rs.getInt(3);
                    int thuong = rs.getInt(4);
                    int tongLuong = rs.getInt(5);
                    Object[] rows = {maNV, hoTen, luong, thuong, tongLuong};
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
    
    private boolean checkBeforeUpdating() {
        String maNV = txtMaNV.getText();
        String hoTen = txtHoTen.getText();
        String luong = txtLuong.getText();
        String thuong = txtThuong.getText();
        
        if(checkEmpty(maNV,"Mã Nhân Viên chưa được nhập!")){
            return true;
        }
        if(!checkDuplicate(maNV, "NhanVien", "maNV")){
            JOptionPane.showMessageDialog(this, "MÃ NHÂN VIÊN này chưa có trong danh sách! "
                    + "Vui lòng chọn Add để lưu Nhân Viên mới!");
            return true;
        }
        
        if(checkEmpty(hoTen,"Tên Nhân Viên chưa được nhập!")){
            return true;
        }
        if(checkEmpty(luong,"Lương Nhân Viên chưa được nhập!")){
            return true;
        }
        if(checkEmpty(thuong,"Thưởng Nhân Viên chưa được nhập!")){
            return true;
        }
        if(Integer.parseInt(luong) < 0){
            JOptionPane.showMessageDialog(this, "LƯƠNG phải lớn hơn hoặc bằng 0! "
                    + "Vui lòng nhập lại lương!");
            return true;
        }
        if(Integer.parseInt(thuong) < 0){
            JOptionPane.showMessageDialog(this, "THƯỞNG phải lớn hơn hoặc bằng 0! "
                    + "Vui lòng nhập lại thưởng!");
            return true;
        }
        return false;
    }
    
    private void updateNhanVienTable() {
        String maNV = txtMaNV.getText();
        String hoTen = txtHoTen.getText();
        int luong = Integer.parseInt(txtLuong.getText());
        int thuong = Integer.parseInt(txtThuong.getText());
        
        this.findIndex(maNV);
        try {
            String sql = "Update NhanVien set maNV = ?, hoTen = ?, luong = ?, thuong = ?"
                        + " Where maNV = ? ;";
            PreparedStatement st = con.prepareStatement(sql); 
            st.setString(1, maNV); 
            st.setString(2, hoTen);
            st.setInt(3, luong);
            st.setInt(4, thuong);
            st.setString(5, maNV);
            
            int rs = st.executeUpdate(); 
      
            if (rs > 0) {
                tblNhanVien.setValueAt(maNV, index, 0);
                tblNhanVien.setValueAt(hoTen, index, 1);
                tblNhanVien.setValueAt(luong, index, 2);
                tblNhanVien.setValueAt(thuong, index, 3);
                tblNhanVien.setValueAt(thuong+luong, index, 4);
                tblNhanVien.setRowSelectionInterval(index, index); 
                JOptionPane.showMessageDialog(this, "Nhân viên " + txtMaNV.getText() + " đã được cập nhật!");
            }
            st.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi:" + ex);
            System.exit(0);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        txtHoTen = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtLuong = new javax.swing.JTextField();
        txtThuong = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        btnClear = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnFilter = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("Mã NV:");

        jLabel3.setText("Họ Tên:");

        jLabel4.setText("Lương:");

        jLabel5.setText("Thưởng:");

        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblNhanVien);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUẢN LÝ LƯƠNG");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilter))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtThuong, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(56, 56, 56)))
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtThuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnAdd)
                    .addComponent(btnFilter)
                    .addComponent(btnUpdate))
                .addGap(94, 94, 94))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblNhanVienMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNhanVienMouseReleased
        index = tblNhanVien.getSelectedRow();
        this.showDetail();
    }//GEN-LAST:event_tblNhanVienMouseReleased

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        this.clearJTextField();
        loadDataToTable();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        loadDataToTable();
        if(this.checkBeforeSaving()){
            return;
        }
        this.saveDataToNhanVienTable();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        this.filter();
        index = 0;
        tblNhanVien.setRowSelectionInterval(index, index);
        showDetail();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
//        loadDataToTable();
        if(this.checkBeforeUpdating()){
            return;
        }
        this.updateNhanVienTable();
    }//GEN-LAST:event_btnUpdateActionPerformed
    
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtLuong;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtThuong;
    // End of variables declaration//GEN-END:variables
}
