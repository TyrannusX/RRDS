package rrdsclient;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author dariusstephen
 */
public class HomeFrame extends javax.swing.JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String response;
    private int filenum;
    private DateFormat df;
    private SimpleDateFormat sf;
    private StringBuilder sb;
    private Date emaildate;
    private DefaultListModel listModel;
    
    /**
     * Creates new form HomeFrame
     * @param socketIn
     */
    
    public HomeFrame(Socket socketIn) {
        try {
            socket = socketIn;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            initComponents();
            hideLabels();
            initVariables();
        } 
        catch (IOException e) {
            System.out.println("inithome failed");
        }
    }
    private void hideLabels() {
        lblSender.setText("");
        lblFrom.setText("");
        lblTo.setText("");
        lblSubject.setText("");
        lblDate.setText("");
        lblBody.setText("");
    }
    
    private void initVariables() {
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sf = new SimpleDateFormat("mm/dd/yy HH:mm");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        tbHome = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        lblTest = new javax.swing.JLabel();
        panelHome = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listFolder = new javax.swing.JList();
        spSubject = new javax.swing.JScrollPane();
        listSubject = new javax.swing.JList();
        panelContent = new javax.swing.JPanel();
        lblSender = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        lblTo = new javax.swing.JLabel();
        lblSubject = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblBody = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(45, 51, 56));
        setMinimumSize(new java.awt.Dimension(670, 450));

        tbHome.setRollover(true);

        btnNew.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        tbHome.add(btnNew);

        lblTest.setText("Test");
        tbHome.add(lblTest);
        lblTest.getAccessibleContext().setAccessibleName("lblTest");

        listFolder.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Inbox", "Sent", "Trash" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listFolder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFolderMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(listFolder);

        spSubject.setViewportView(listSubject);

        panelContent.setBackground(new java.awt.Color(255, 255, 255));
        panelContent.setForeground(new java.awt.Color(255, 255, 255));

        lblSender.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblSender.setText("<sender>");

        lblFrom.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblFrom.setText("<from>");

        lblTo.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblTo.setText("<to>");

        lblSubject.setFont(new java.awt.Font("Lucida Grande", 1, 15)); // NOI18N
        lblSubject.setText("<Subject>");

        lblDate.setText("<date>");

        lblBody.setText("<body>");

        javax.swing.GroupLayout panelContentLayout = new javax.swing.GroupLayout(panelContent);
        panelContent.setLayout(panelContentLayout);
        panelContentLayout.setHorizontalGroup(
            panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(panelContentLayout.createSequentialGroup()
                        .addGroup(panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFrom)
                            .addComponent(lblSubject)
                            .addComponent(lblBody))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelContentLayout.createSequentialGroup()
                        .addComponent(lblSender)
                        .addGap(18, 18, 18)
                        .addComponent(lblTo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblDate)))
                .addContainerGap())
        );
        panelContentLayout.setVerticalGroup(
            panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSender)
                    .addComponent(lblTo)
                    .addComponent(lblDate))
                .addGap(5, 5, 5)
                .addComponent(lblFrom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubject)
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBody)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelHomeLayout = new javax.swing.GroupLayout(panelHome);
        panelHome.setLayout(panelHomeLayout);
        panelHomeLayout.setHorizontalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelHomeLayout.setVerticalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHomeLayout.createSequentialGroup()
                .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spSubject, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelContent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tbHome, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                        .addGap(561, 561, 561))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbHome, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        String haha = btnNew.getText();
        lblTest.setText(haha);
    }//GEN-LAST:event_btnNewActionPerformed

    private void listFolderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFolderMouseClicked
        // TODO add your handling code here:
        int currentIndex = listFolder.getSelectedIndex();
        
        switch(currentIndex) {
            case 0:
                out.println("getinbox");
                break;
            case 1:
                out.println("getsent");
                break;
            case 2:
                out.println("gettrash");
                break;
            default:
                out.println("noreq");
                break;
        }
        
        // Get the number of files from server
        try {
            response = in.readLine();
            filenum = Integer.parseInt(response);
        } catch (IOException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Instantiate listmodel for jlist and its stringbuilder
        listModel = new DefaultListModel();
        sb = new StringBuilder();
        
        // Process requested emails from server
        for(int i = 0; i < filenum; i++) {
            try {
                sb.append("<html>");
                // Get the datetime of message
                response = in.readLine();
                try {
                    emaildate = df.parse(response);
                    sb.append(emaildate.toString());
                } catch (ParseException ex) {
                    Logger.getLogger(HomeFrame.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                sb.append("<br>");
                
                // Get the 'to' field of the message
                response = in.readLine();
                sb.append(response);
                sb.append("<br>");

                // Get the 'from' field of the message
                response = in.readLine();
                sb.append(response);
                sb.append("<br>");

                // Get the subject of the message
                response = in.readLine();
                sb.append(response);
                sb.append("<br>");

                // Get the body of the message
                response = in.readLine();
                sb.append(response);
                sb.append("<br>");
                sb.append("</html>");
            }
            catch (IOException e) {
                System.out.println("Error occurred while reading from server");
            }
            // Add stringbuilder to listmodel
            listModel.addElement(sb.toString());
            
            // Reset stringbuilder
            sb.setLength(0);
        } // forloop end
        listSubject.setModel(listModel);
    }//GEN-LAST:event_listFolderMouseClicked

    /*
    public static void main(String args[]) {     
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBody;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblSender;
    private javax.swing.JLabel lblSubject;
    private javax.swing.JLabel lblTest;
    private javax.swing.JLabel lblTo;
    private javax.swing.JList listFolder;
    private javax.swing.JList listSubject;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelHome;
    private javax.swing.JScrollPane spSubject;
    private javax.swing.JToolBar tbHome;
    // End of variables declaration//GEN-END:variables
}
