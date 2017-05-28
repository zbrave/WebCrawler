package edu.uci.ics.crawler4j.basic;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UI extends JFrame {
	
	private static final Logger logger =
	        LoggerFactory.getLogger(LocalDataCollectorController.class);
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setTitle("Crawler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 368, 262);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JCheckBox chckbxTeknosa = new JCheckBox("Teknosa");
		chckbxTeknosa.setBounds(8, 39, 112, 24);
		contentPane.add(chckbxTeknosa);
		
		JCheckBox chckbxMediamarkt = new JCheckBox("MediaMarkt");
		chckbxMediamarkt.setBounds(124, 39, 112, 24);
		contentPane.add(chckbxMediamarkt);
		
		JCheckBox chckbxVatanComp = new JCheckBox("Vatan Comp.");
		chckbxVatanComp.setBounds(240, 39, 112, 24);
		contentPane.add(chckbxVatanComp);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(186, 74, 42, 25);
		contentPane.add(comboBox);
		
		JLabel lblCrawlerNumber = new JLabel("Crawler number:");
		lblCrawlerNumber.setBounds(66, 78, 102, 16);
		contentPane.add(lblCrawlerNumber);
		
		textField = new JTextField();
		textField.setText("10");
		textField.setBounds(186, 104, 114, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblMaxFetch = new JLabel("Max Fetch:");
		lblMaxFetch.setBounds(66, 106, 102, 16);
		contentPane.add(lblMaxFetch);
		
		JLabel lblPoliteness = new JLabel("Politeness:");
		lblPoliteness.setBounds(66, 134, 73, 16);
		contentPane.add(lblPoliteness);
		
		textField_1 = new JTextField();
		textField_1.setText("200");
		textField_1.setBounds(186, 132, 114, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String rootFolder = "/data";
		        int numberOfCrawlers = Integer.parseInt((String) comboBox.getSelectedItem());

		        CrawlConfig config = new CrawlConfig();
		        config.setCrawlStorageFolder(rootFolder);
		        config.setMaxPagesToFetch(Integer.parseInt((String) textField.getText()));
		        config.setPolitenessDelay(Integer.parseInt((String) textField_1.getText()));

		        PageFetcher pageFetcher = new PageFetcher(config);
		        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		        CrawlController controller;
				try {
					controller = new CrawlController(config, pageFetcher, robotstxtServer);
					if (chckbxTeknosa.isSelected()){
						controller.addSeed("http://www.istanbulbilisim.com.tr/");
						controller.start(LocalDataCollectorCrawlerTeknosa.class, numberOfCrawlers);
					}
					if (chckbxMediamarkt.isSelected()) {
						controller.addSeed("http://www.mediamarkt.com.tr/tr/category/_cep-telefonlar%C4%B1-504171.html");
						controller.start(LocalDataCollectorCrawlerMediaMarkt.class, numberOfCrawlers);
					}
					if (chckbxVatanComp.isSelected()) {
						controller.addSeed("http://www.vatanbilgisayar.com/");
						controller.start(LocalDataCollectorCrawlerVatan.class, numberOfCrawlers);
					}
			        
			        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
			        long totalLinks = 0;
			        long totalTextSize = 0;
			        int totalProcessedPages = 0;
			        for (Object localData : crawlersLocalData) {
			            CrawlStat stat = (CrawlStat) localData;
			            totalLinks += stat.getTotalLinks();
			            totalTextSize += stat.getTotalTextSize();
			            totalProcessedPages += stat.getTotalProcessedPages();
			        }
			        DbDAO d = new DbDAO();
			        d.hits();
			        logger.info("Aggregated Statistics:");
			        logger.info("\tProcessed Pages: {}", totalProcessedPages);
			        logger.info("\tTotal Links found: {}", totalLinks);
			        logger.info("\tTotal Text Size: {}", totalTextSize);
//			        File htmlFile = new File("output.html");
//			        Desktop.getDesktop().browse(htmlFile.toURI());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSearch.setBounds(8, 184, 332, 26);
		contentPane.add(btnSearch);
		
		
		
		
	}
}
