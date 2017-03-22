/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HostInfoView.java
 *
 * Created on 25 oct. 2010, 16:08:27
 */

package net.atomique.ksar.UI;

import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.XML.HostInfo;

/**
 *
 * @author u098006
 */
public class HostInfoView extends javax.swing.JDialog {

    /** Creates new form HostInfoView */
    public HostInfoView(java.awt.Frame parent, HostInfo tmphostinfo) {
       
        super(parent, true);
         hostinfo = tmphostinfo;
        initComponents();
        Hostname.setText(hostinfo.getHostname());
        AliasField.setText(hostinfo.getAlias());
        jTextArea1.setText(hostinfo.getDescription());
        memboxcombo.setSelectedItem(hostinfo.getMemBlockSize().toString());
        setLocationRelativeTo(GlobalOptions.getInstance().getUI());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        hostnamePanel = new javax.swing.JPanel();
        HostNameLabel = new javax.swing.JLabel();
        Hostname = new javax.swing.JLabel();
        AliasPanel = new javax.swing.JPanel();
        AliasLabel = new javax.swing.JLabel();
        AliasField = new javax.swing.JTextField();
        DescriptionPanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        MemoryPanel = new javax.swing.JPanel();
        memBlockSizeLabel = new javax.swing.JLabel();
        memboxcombo = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        OkButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Host Information");

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        hostnamePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        HostNameLabel.setLabelFor(Hostname);
        HostNameLabel.setText("Hostname:");
        hostnamePanel.add(HostNameLabel);

        Hostname.setText("hostname");
        hostnamePanel.add(Hostname);

        jPanel1.add(hostnamePanel);

        AliasPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        AliasLabel.setText("Alias");
        AliasLabel.setPreferredSize(new java.awt.Dimension(52, 14));
        AliasPanel.add(AliasLabel);

        AliasField.setMinimumSize(new java.awt.Dimension(120, 20));
        AliasField.setPreferredSize(new java.awt.Dimension(120, 20));
        AliasPanel.add(AliasField);

        jPanel1.add(AliasPanel);

        DescriptionPanel.setLayout(new javax.swing.BoxLayout(DescriptionPanel, javax.swing.BoxLayout.PAGE_AXIS));

        descriptionLabel.setText("Description");
        descriptionLabel.setPreferredSize(new java.awt.Dimension(52, 14));
        DescriptionPanel.add(descriptionLabel);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 200));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setPreferredSize(new java.awt.Dimension(300, 200));
        jScrollPane1.setViewportView(jTextArea1);

        DescriptionPanel.add(jScrollPane1);

        jPanel1.add(DescriptionPanel);

        MemoryPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        memBlockSizeLabel.setText("Memory Block Size:");
        MemoryPanel.add(memBlockSizeLabel);

        memboxcombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1024", "2048", "4096", "8192", "16384" }));
        MemoryPanel.add(memboxcombo);

        jPanel1.add(MemoryPanel);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        OkButton.setText("Ok");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });
        jPanel2.add(OkButton);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        this.dispose();
        hostinfo.setAlias(AliasField.getText());
        hostinfo.setDescription(jTextArea1.getText());
        hostinfo.setMemBlockSize(memboxcombo.getSelectedItem().toString());
        GlobalOptions.getInstance().addHostInfo(hostinfo);
    }//GEN-LAST:event_OkButtonActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AliasField;
    private javax.swing.JLabel AliasLabel;
    private javax.swing.JPanel AliasPanel;
    private javax.swing.JPanel DescriptionPanel;
    private javax.swing.JLabel HostNameLabel;
    private javax.swing.JLabel Hostname;
    private javax.swing.JPanel MemoryPanel;
    private javax.swing.JButton OkButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel hostnamePanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel memBlockSizeLabel;
    private javax.swing.JComboBox memboxcombo;
    // End of variables declaration//GEN-END:variables
    HostInfo hostinfo = null;
}
