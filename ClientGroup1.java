import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class ClientGroup1  extends JFrame implements MouseListener, ActionListener {
	
	
	/***************************************************************クラスの属性****************************************************
	 *******************************************************************************************************************************/
	
	/************最初に覚えておく数************/
	
	private Player itsme = new Player();                   //自分のプレイヤークラスのオブジェクト
	private Player enemy;                   //相手のプレイヤークラスのオブジェクト
	
	private static String address;
	private static int bangou;
	
	/************Swing関係の変数************/
	private CardLayout cl;
	
	private JLabel label,label2,label3,label4,label4_1,label4_2,label5,label6,label6_1,label6_2,label7,label7_1,label7_2,label8,label9,label10,label11,labelwin,labelscore;
	private String text="";
	private String textColor="";
	private String textTeban="";
	private String textwinorlose = "";
	private String textscore = "";
	private String textnow;
	
	private JTextField text1;
	private JRadioButton[] radio;
    private JPanel container;
    private JPanel boardPanel;          // ← 追加：対戦画面専用パネル
    private JPanel BOARD;
    private JPanel p3,p4,p6,error;
    private JLabel imageLabel;
    private JLabel imageLabel2;
    private JLabel imageLabel3;
    private JLabel NowScore;
    JButton b1;
    JButton b2;
    JButton b3;
    JButton b4;
    JButton b5;
    JButton b6;
    JButton b7;
    JButton b6_1;
    JButton b7_1;
    JButton b8;
    JButton b9;
    JButton b10;
    JButton b11;
    
    JButton f1,f2,f3,f4,f5,f6;
    
    JButton bend;
    JButton bagain;
    
    private JButton pass;     //停止、スキップ用ボタン
	private JLabel colorLabel;      // 色表示用ラベル
	private JLabel turnLabel;       // 手番表示用ラベル
	private JLabel messageLabel;    //対戦中メッセージラベル
	private ImageIcon whiteIcon;
	private ImageIcon blackIcon;
	private ImageIcon boardIcon;
	private ImageIcon PlaceIcon;
	private ImageIcon EdgeIcon;
	private ImageIcon Rainbow;
	private ImageIcon EdgeIcon_a;
	private ImageIcon EdgeIcon_b;
	private ImageIcon EdgeIcon_aa;
	private ImageIcon EdgeIcon_bb;
    
	/************サーバー通信関係の変数************/
	private Socket socket;
	private PrintWriter out;        //データ送信用オブジェクト
	private Receiver receiver;      //データ受信用オブジェクト
	private JButton buttonArray[];  //オセロ盤用のボタン配列
	String [] grids = 
		{"board","board","board","board","board","board","board","board",
		"board","board","board","board","board","board","board","board",
		"board","board","board","board","board","board","board","board",
		"board","board","board","black","white","board","board","board",
		"board","board","board","white","black","board","board","board",
		"board","board","board","board","board","board","board","board",
		"board","board","board","board","board","board","board","board",
		"board","board","board","board","board","board","board","board"};
		
	/************オセロクラスのオブジェクト************/
	
	Othello othello = new Othello();
	
	/**********状態関係のflag達************/
	
	private int handhi;                     //ハンディキャップ用の変数 0-4
	private int handhiKIOKU;            //一回目のハンディを記憶しておくよう
	private int xy=0;                     //座標を格納する変数
	private int HANDIcounter;                      //ハンディキャップを何回受け取るかを示す
    private boolean placeflag;
	private int flag_11=0;
	private int flaga;
	private int flagb;
	private boolean permit;
	private int OFUZAKEflag;
	private boolean alreadySentEnd = false; // クラスのメンバ変数として宣言しておく
	private boolean ENDGAME = false;              //一回終了画面になったかどうか
	private boolean Surrender = false;
	
    private int ConnectCounter = 0;
	
	/**********特にアニメーション用のflag達************/
	private boolean animationInProgress = false;
	private final Queue<String> messageQueue = new LinkedList<>();
	
	
	/***************************************************************コンストラクタ****************************************************
	 *******************************************************************************************************************************/
	
	public ClientGroup1(String title) {
		super(title);
		itsme=new Player();
		enemy = new Player();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,700);
		setResizable(false);
		
		// CardLayoutを使った画面コンテナ
        container = new JPanel(new CardLayout());
        
		cl = (CardLayout)(container.getLayout());
		
		
		//最初の最初の画面**********************************************************************************
		JPanel Start = new JPanel();
		Start.setLayout(null);

		// 背景画像
		ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("OthelloPocket.png"));
		Image img = backgroundIcon.getImage();
		Image scaledImg = img.getScaledInstance(400, 700, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaledImg);

		JLabel backgroundLabel = new JLabel(scaledIcon);
		backgroundLabel.setBounds(0, 0, 400, 700);
		

		// Othello Pocketロゴ（ふわふわ動く）
		ImageIcon logoIcon = new ImageIcon(getClass().getResource("OthelloPocketTitle.png"));
		Image logoImg = logoIcon.getImage();
		Image scaledLogoImg = logoImg.getScaledInstance(250, 125, Image.SCALE_SMOOTH); // ★ 縮小サイズ指定
		ImageIcon scaledLogoIcon = new ImageIcon(scaledLogoImg);

		// ラベルに設定
		int y_3 = 160;     //盾を調整する座標
		int x_3 = 80;       //横を調整する座標
		JLabel logoLabel = new JLabel(scaledLogoIcon);
		logoLabel.setBounds(x_3, y_3, 250, 125); // ★ 同じサイズで設定
		
		// アニメーション設定
		Timer timer = new Timer(30, null);
		final int[] dy = {1};                //yの増加量
		final int[] y_1 = {y_3}; // 初期Y位置

		timer.addActionListener(e -> {
		    y_1[0] += dy[0];
		    if (y_1[0] > (y_3+10) || y_1[0] < (y_3-10)) {
		        dy[0] = -dy[0];
		    }
		    logoLabel.setLocation(x_3, y_1[0]);
		});
		timer.start();

		// マウスクリックで画面遷移
		Start.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        cl.show(container, "screen1");
		    }
		});
		
		// 利用規約のパネル
		JPanel rulePanel = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2d = (Graphics2D) g;
		        g2d.setColor(new Color(0, 0, 0, 180));
		        g2d.fillRect(0, 0, getWidth(), getHeight());
		    }
		};
		rulePanel.setLayout(new BorderLayout());
		rulePanel.setBounds(20, 400, 360, 200); // 位置・サイズ調整
		rulePanel.setOpaque(false);
		rulePanel.setVisible(false); // 最初は非表示！

		// テキスト
		JTextArea ruleText = new JTextArea(
		    "====== 利用規約 ======\n" +
		    "・このゲームはオセロゲームです。\n" +
		    "・たのしんでね。\n" +
		    "・でゅふふ笑"
		);
		ruleText.setOpaque(false);
		ruleText.setForeground(Color.WHITE);
		ruleText.setFont(new Font("MS ゴシック", Font.PLAIN, 12));
		ruleText.setEditable(false);
		rulePanel.add(ruleText, BorderLayout.CENTER);

		// 丸ボタン作成
		RoundButton ruleButton = new RoundButton("！");
		ruleButton.setBounds(330, 20, 50, 50); // ←位置調整
		ruleButton.addActionListener(e -> {
		    rulePanel.setVisible(!rulePanel.isVisible());
		});

		// パネルに追加
		Start.add(ruleButton);
		Start.add(rulePanel);
		
		Start.add(logoLabel);
		Start.add(backgroundLabel);

		

		  //画面１(名前入力)********************************************************************************
		JPanel p1 = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        ImageIcon bgIcon = new ImageIcon(getClass().getResource("back.png")); // 背景画像を指定
		        g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
		    }
		};
		p1.setLayout(null); // 手動配置に切り替え
		itsme.name="";

		// ラベル
		label = new JLabel("名前を入力してください");
		label.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 20));
		label.setForeground(Color.BLACK);
		label.setBounds(100, 250, 250, 30);
		p1.add(label);

		// テキストフィールド
		text1 = new JTextField();
		text1.setColumns(10);
		text1.setHorizontalAlignment(JTextField.CENTER);
		text1.setFont(new Font("メイリオ", Font.PLAIN, 18));
		text1.setBounds(100, 300, 200, 30);
		p1.add(text1);

		// 「決定」ボタン
		b1 = new JButton("決定");
		b1.setFont(new Font("メイリオ", Font.BOLD, 16));
		b1.setBounds(100, 350, 90, 30);
		b1.addActionListener(this);
		p1.add(b1);

		// 「次の画面へ」ボタン
		b2 = new JButton("次へ");
		b2.setFont(new Font("メイリオ", Font.BOLD, 16));
		b2.setBounds(210, 350, 90, 30);
		b2.setEnabled(false);
		b2.addActionListener(this);
		p1.add(b2);

              
		//画面２(レベル選択)********************************************************************************
        
		JPanel p2 = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        ImageIcon bg = new ImageIcon(getClass().getResource("back.png")); // 背景画像を指定
		        g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
		    }
		};
		p2.setLayout(null); // 自由配置に変更

		// タイトル
		JLabel title2 = new JLabel("レベルを選択してください");
		title2.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 24));
		title2.setForeground(Color.BLACK);
		title2.setBounds(50, 130, 300, 40);
		p2.add(title2);

		// レベルラジオボタンを縦に配置
		radio = new JRadioButton[5];
		String[] levels = {"レベル１：超初心者", "レベル２：初心者", "レベル３：中級者", "レベル４：上級者", "レベル５：神"};
		ButtonGroup bgroup = new ButtonGroup();
		int y = 200;

		for (int i = 0; i < 5; i++) {
		    radio[i] = new JRadioButton(levels[i]);
		    radio[i].setOpaque(false); // 背景透過
		    radio[i].setForeground(Color.BLACK);
		    radio[i].setFont(new Font("メイリオ", Font.PLAIN, 18));
		    radio[i].setBounds(100, y, 300, 30);
		    if (i == 0) radio[i].setSelected(true);
		    bgroup.add(radio[i]);
		    p2.add(radio[i]);
		    y += 40;
		}

		// 保存ボタン
		b3 = new JButton("保存");
		b3.setBounds(100, 420, 100, 30);
		b3.addActionListener(this);
		p2.add(b3);

		// 前の画面へ
		b4 = new JButton("前へ");
		b4.setBounds(100, 520, 100, 30);
		b4.addActionListener(this);
		p2.add(b4);

		// 次の画面へ
		b5 = new JButton("次へ");
		b5.setBounds(210, 520, 100, 30);
		b5.setEnabled(false);
		b5.addActionListener(this);
		p2.add(b5);
		
		//画面おふざけ(スキン選択)********************************************************************************
        
				JPanel SKIN = new JPanel() {
				    @Override
				    protected void paintComponent(Graphics g) {
				        super.paintComponent(g);
				        ImageIcon bg = new ImageIcon(getClass().getResource("back.png")); // 背景画像を指定
				        g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
				    }
				};
				SKIN.setLayout(null); // 自由配置に変更

				// タイトル
				JLabel title4 = new JLabel("駒のSKINを選択してください...");
				title4.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 24));
				title4.setForeground(Color.BLACK);
				title4.setBounds(50, 130, 300, 40);
				SKIN.add(title4);
				
				//スキンの画像を変える
				ImageIcon SKIN1 = new ImageIcon(getClass().getResource("White.jpg"));
				ImageIcon SKIN2 = new ImageIcon(getClass().getResource("Black.jpg"));
				ImageIcon SKIN3 = new ImageIcon(getClass().getResource("GreenFrame.jpg"));

				// スキン１ボタン
				f1 = new JButton("SKIN1");
				f1.setBounds(100, 220, 100, 30);
				f1.addActionListener(this);
				SKIN.add(f1);
				// スキン２ボタン
				f2 = new JButton("SKIN2");
				f2.setBounds(210, 220, 100, 30);
				f2.addActionListener(this);
				SKIN.add(f2);
				// スキン３ボタン
				f3 = new JButton("SKIN3");
				f3.setBounds(320, 220, 100, 30);
				f3.addActionListener(this);
				SKIN.add(f3);

				// 保存ボタン
				f4 = new JButton("保存");
				f4.setBounds(100, 520, 100, 30);
				f4.addActionListener(this);
				SKIN.add(f4);

				// 前の画面へ
				f5 = new JButton("前へ");
				f5.setBounds(210, 520, 100, 30);
				f5.addActionListener(this);
				SKIN.add(f5);

				// 次の画面へ
				f6 = new JButton("次へ");
				f6.setBounds(320, 520, 100, 30);
				f6.setEnabled(false);
				f6.addActionListener(this);
				SKIN.add(f6);


		
       //画面２-1(ハンディキャップの確認)********************************************************************************
        
				JPanel p2_1 = new JPanel() {
				    @Override
				    protected void paintComponent(Graphics g) {
				        super.paintComponent(g);
				        ImageIcon bg = new ImageIcon(getClass().getResource("stay.png")); // 背景画像（用意してね）
				        g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
				    }
				};
				p2_1.setLayout(null); // 自由配置で柔軟に

				// タイトルラベル
				JLabel label2 = new JLabel("ハンディキャップありの対戦にしますか？");
				label2.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 22));
				label2.setForeground(Color.RED);
				label2.setBounds(10, 220, 400, 30);
				p2_1.add(label2);
				
				// タイトルラベル
				JLabel label12 = new JLabel("<html>※レベル差に対応した子局の初期盤面が適用され、<br>同数の時レベルの低いほうが勝ちになります</html>");
				label12.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 15));
				label12.setForeground(Color.BLACK);
				label12.setBounds(40, 285, 400, 30);
				p2_1.add(label12);

				// YESボタン
				b8 = new JButton("YES");
				b8.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
				b8.setBounds(80, 335, 100, 40);
				b8.addActionListener(this);
				p2_1.add(b8);

				// NOボタン
				b9 = new JButton("NO");
				b9.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
				b9.setBounds(230, 335, 100, 40);
				b9.addActionListener(this);
				p2_1.add(b9);
    
        
		
		//画面３-1(ハンディキャップ確認　黒側)********************************************************************************
		// 背景画像を描画するカスタムパネル
		p3 = new JPanel() {
		    Image bg = new ImageIcon(getClass().getResource("stay.png")).getImage(); // 背景画像（差し替え可）

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
		    }
		};
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		p3.setOpaque(false); // 念のため透明化

		// ラベルやボタンを乗せるための半透明パネル
		JPanel overlayPanel = new JPanel();
		overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
		overlayPanel.setOpaque(false); // 背景を透明にして、背景画像を見せる
		overlayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// ラベル1：メッセージ
		label8 = new JLabel(text);
		label8.setFont(new Font("メイリオ", Font.BOLD, 16));
		label8.setForeground(Color.RED);
		label8.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel.add(Box.createVerticalStrut(20));
		overlayPanel.add(label8);

		// ラベル2：色情報
		label3 = new JLabel("<html>Your color is 黒：<br>あなたはハンディキャップを掛けられる側です！</html>");
		label3.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.PLAIN, 18));
		label3.setForeground(Color.BLACK);
		label3.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel.add(Box.createVerticalStrut(10));
		overlayPanel.add(label3);

		// ★ ハンディキャップ画像 ★
		ImageIcon initialIcon = new ImageIcon(getClass().getResource("handhi0.png")); // 初期画像を最初の盤面とする
		Image img3 = initialIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		imageLabel = new JLabel(new ImageIcon(img3));
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel.add(Box.createVerticalStrut(10));
		overlayPanel.add(imageLabel);

		// ラベル3：開始案内
		label4 = new JLabel("この状態でゲームが始まります!");
		label4.setFont(new Font("メイリオ", Font.PLAIN, 20));
		label4.setForeground(Color.BLACK);
		label4.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel.add(Box.createVerticalStrut(10));
		overlayPanel.add(label4);

		// ボタン類（中央に横並び）
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		b6 = new JButton("このままでよい");
		b6.addActionListener(this);
		b7 = new JButton("ハンディキャップを一つ下げる");
		b7.addActionListener(this);
		buttonPanel.add(b6);
		buttonPanel.add(b7);

		overlayPanel.add(Box.createVerticalStrut(20));
		overlayPanel.add(buttonPanel);

		// overlayPanel を p3 に追加
		p3.add(Box.createVerticalGlue()); // 上部余白
		p3.add(overlayPanel);
		p3.add(Box.createVerticalGlue()); // 下部余白

        
        
        
        
        //画面３-2(ハンディキャップ確認　白側)********************************************************************************
		// 背景画像を描画するカスタムパネル
				p4 = new JPanel() {
				    Image bg5 = new ImageIcon(getClass().getResource("stay.png")).getImage(); // 背景画像（差し替え可）

				    @Override
				    protected void paintComponent(Graphics g) {
				        super.paintComponent(g);
				        g.drawImage(bg5, 0, 0, getWidth(), getHeight(), this);
				    }
				};
				p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
				p4.setOpaque(false); // 念のため透明化

				// ラベルやボタンを乗せるための半透明パネル
				JPanel overlayPanel2 = new JPanel();
				overlayPanel2.setLayout(new BoxLayout(overlayPanel2, BoxLayout.Y_AXIS));
				overlayPanel2.setOpaque(false); // 背景を透明にして、背景画像を見せる
				overlayPanel2.setAlignmentX(Component.CENTER_ALIGNMENT);

				// ラベル1：メッセージ
				label7 = new JLabel(text);
				label7.setFont(new Font("メイリオ", Font.BOLD, 16));
				label7.setForeground(Color.RED);
				label7.setAlignmentX(Component.CENTER_ALIGNMENT);
				overlayPanel2.add(Box.createVerticalStrut(20));
				overlayPanel2.add(label7);

				// ラベル2：色情報
				label5 = new JLabel(
					    "<html>Your color is <span style='color:white;'>白</span>：<br>" +
					    "あなたはハンディキャップを掛ける側です！</html>"
					);
				label5.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.PLAIN, 20));
				label5.setForeground(Color.BLACK);
				label5.setAlignmentX(Component.CENTER_ALIGNMENT);
				overlayPanel2.add(Box.createVerticalStrut(10));
				overlayPanel2.add(label5);

				// ★ ハンディキャップ画像 ★
				ImageIcon initialIcon2 = new ImageIcon(getClass().getResource("handhi0.png")); // 初期画像を最初の盤面とする
				Image img4 = initialIcon2.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				imageLabel2 = new JLabel(new ImageIcon(img4));
				imageLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
				overlayPanel2.add(Box.createVerticalStrut(10));
				overlayPanel2.add(imageLabel2);

				// ラベル3：開始案内
				label4_1 = new JLabel("この状態でゲームが始まります!");
				label4_1.setFont(new Font("メイリオ", Font.PLAIN, 20));
				label4_1.setForeground(Color.BLACK);
				label4_1.setAlignmentX(Component.CENTER_ALIGNMENT);
				overlayPanel2.add(Box.createVerticalStrut(10));
				overlayPanel2.add(label4_1);

				// ボタン類（中央に横並び）
				JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
				buttonPanel2.setOpaque(false);
				b6_1 = new JButton("このままでよい");
				b6_1.addActionListener(this);
				b7_1 = new JButton("ハンディキャップを一つ下げる");
				b7_1.addActionListener(this);
				buttonPanel2.add(b6_1);
				buttonPanel2.add(b7_1);

				overlayPanel2.add(Box.createVerticalStrut(20));
				overlayPanel2.add(buttonPanel2);

				// overlayPanel2 を p4 に追加
				p4.add(Box.createVerticalGlue()); // 上部余白
				p4.add(overlayPanel2);
				p4.add(Box.createVerticalGlue()); // 下部余白
        
      //画面３-3(ハンディキャップ最終)********************************************************************************
        p6 = new JPanel() {
		    Image bg5 = new ImageIcon(getClass().getResource("back.png")).getImage(); // 背景画像（差し替え可）

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(bg5, 0, 0, getWidth(), getHeight(), this);
		    }
		};
		p6.setLayout(new BoxLayout(p6, BoxLayout.Y_AXIS));
		p6.setOpaque(false); // 念のため透明化
		
		// ラベルやボタンを乗せるための半透明パネル
		JPanel overlayPanel3 = new JPanel();
		overlayPanel3.setLayout(new BoxLayout(overlayPanel3, BoxLayout.Y_AXIS));
		overlayPanel3.setOpaque(false); // 背景を透明にして、背景画像を見せる
		overlayPanel3.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// ラベル1：メッセージ
		label10 = new JLabel(text);
		label10.setFont(new Font("メイリオ", Font.BOLD, 16));
		label10.setForeground(Color.RED);
		label10.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel3.add(Box.createVerticalStrut(20));
		overlayPanel3.add(label10);

		// ラベル2：最終確認
		label9 = new JLabel("このハンディキャップで対戦を開始します...");
		label9.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.PLAIN, 20));
		label9.setForeground(Color.BLACK);
		label9.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel3.add(Box.createVerticalStrut(10));
		overlayPanel3.add(label9);
		
		// ★ ハンディキャップ画像 ★
		ImageIcon initialIcon3 = new ImageIcon(getClass().getResource("handhi0.png")); // 初期画像を最初の盤面とする
		Image img5 = initialIcon3.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		imageLabel3 = new JLabel(new ImageIcon(img5));
		imageLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
		overlayPanel3.add(Box.createVerticalStrut(10));
		overlayPanel3.add(imageLabel3);
		
		// overlayPanel3 を p6 に追加
		p6.add(Box.createVerticalGlue()); // 上部余白
		p6.add(overlayPanel3);
		p6.add(Box.createVerticalGlue()); // 下部余白

		
		//画面４(仮対局画面)********************************************************************************
        JPanel p5 = new JPanel();
        p5.setLayout(new FlowLayout());
        label6_2 = new JLabel("対局画面ダヨーン！");
        p5.add(label6_2);
		
		//画面５(対局終了画面)********************************************************************************
       JPanel END = new JPanel(){
		    Image bg5 = new ImageIcon(getClass().getResource("stay.png")).getImage(); // 背景画像（差し替え可）

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(bg5, 0, 0, getWidth(), getHeight(), this);
		    }
		};
		
       END.setLayout(null);
       
    // BOARD（盤面パネル）
       BOARD = new JPanel();
       BOARD.setLayout(null);
       BOARD.setBounds(100, 60, 200, 200);  // ←盤面を中央寄りに
       
       WritingWinorLoseBoard(8, BOARD,grids);
       END.add(BOARD);

       // 勝敗ラベル
       labelwin = new JLabel(textwinorlose);
       labelwin.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 20));
       labelwin.setForeground(Color.RED);
       labelwin.setBounds(140, 270, 300, 30);  // ←盤面の下に配置
       END.add(labelwin);

       // スコアラベル
       labelscore = new JLabel(textscore);
       labelscore.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 18));
       labelscore.setForeground(Color.BLACK);
       labelscore.setBounds(120, 300, 300, 30);
       END.add(labelscore);

       // YESボタン（終わる）
       bend = new JButton("終わる");
       bend.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
       bend.setBounds(80, 400, 100, 40);  // ←下部に寄せて配置
       bend.addActionListener(this);
       END.add(bend);

       // NOボタン（もう一度）
       bagain = new JButton("もう一度");
       bagain.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
       bagain.setBounds(200, 400, 120, 40);
       bagain.addActionListener(this);
       END.add(bagain);
	    
        
        //共通画面(待機画面)***************************************************************************
    		JPanel stay = new JPanel() {
    		    private int currentStep = 0;
    		    private Timer animationTimer;
    		    private Timer tipsTimer;
    		    private int currentTip = 0;
    		    private JLabel tipsLabel;

    		    {
    		        setBackground(Color.BLACK);
    		        setLayout(null);

    		        // Now Loading ラベル
    		        JLabel label6 = new JLabel("Now Loading...");
    		        label6.setForeground(Color.BLACK);
    		        label6.setFont(new Font("MS ゴシック", Font.BOLD, 22));
    		        label6.setBounds(120, 130, 300, 30); // 上の方に配置
    		        add(label6);

    		        // Tips画像用ラベル
    		        tipsLabel = new JLabel();
    		        tipsLabel.setBounds(18, 200, 350, 350); // 中央に配置（適宜調整）
    		        add(tipsLabel);

    		        // Tips画像の配列
    		        ImageIcon[] tipsImages = {
    		            loadScaledIcon("rule1.jpg"),
    		            loadScaledIcon("rule2.jpg"),
    		            loadScaledIcon("rule3.jpg"),
    		            loadScaledIcon("rule4.jpg")
    		        };

    		        // Tips切り替えタイマー（4秒ごと）
    		        tipsTimer = new Timer(4000, e -> {
    		            tipsLabel.setIcon(tipsImages[currentTip]);
    		            currentTip = (currentTip + 1) % tipsImages.length;
    		        });
    		        tipsTimer.start();

    		        // 3点アニメーションタイマー
    		        animationTimer = new Timer(500, e -> {
    		            currentStep = (currentStep + 1) % 4;
    		            repaint();
    		        });
    		        animationTimer.start();
    		    }

    		    @Override
    		    protected void paintComponent(Graphics g) {
    		        super.paintComponent(g);
    		        Graphics2D g2 = (Graphics2D) g;
    		        ImageIcon bg = new ImageIcon(getClass().getResource("stay.png"));
    		        g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);

    		        int circleSize = 20;
    		        int gap = 25;
    		        int startX = (getWidth() - (circleSize * 3 + gap * 2)) / 2;
    		        int y = 80;

    		        for (int i = 0; i < 3; i++) {
    		            g2.setColor(i < currentStep ? Color.BLACK : Color.WHITE);
    		            g2.fillOval(startX + i * (circleSize + gap), y, circleSize, circleSize);
    		        }
    		    }

    		    // アイコン読み込み・リサイズ
    		    private ImageIcon loadScaledIcon(String filename) {
    		        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
    		        Image image = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH); // 説明画像の大きさ
    		        return new ImageIcon(image);
    		    }
    		};

        
      //共通画面(接続切断画面)***************************************************************************
	    
        error = new JPanel() {
		    Image peg = new ImageIcon(getClass().getResource("stay.png")).getImage(); // 背景画像（差し替え可）

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(peg, 0, 0, getWidth(), getHeight(), this);
		    }
		};
		
		error.setLayout(null); // 自由配置で柔軟に

		// タイトルラベル
		label11 = new JLabel("接続できませんでした...");
		label11.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 20));
		label11.setForeground(Color.RED);
		label11.setBounds(40, 230, 400, 30);
		error.add(label11);

		// YESボタン
		b11 = new JButton("戻る");
		b11.setFont(new Font("MS UI Gothic", Font.BOLD, 18));
		b11.setBounds(80, 300, 100, 40);
		b11.addActionListener(this);
		error.add(b11);

		
        
        // 画面をコンテナに追加（名前付き）
        container.add(Start, "Start");
        container.add(p1, "screen1");
        container.add(p2, "screen2");
        container.add(p2_1, "screen2_1");

        container.add(p3, "screen3");
        container.add(p4, "screen4");
        container.add(p5, "test");
        container.add(p6, "screen6");
        container.add(END, "END");                         ////ゲーム終了画面
        container.add(error, "error");
        
        boardPanel =  new JPanel(null){
		    Image bg5 = new ImageIcon(getClass().getResource("stay.png")).getImage(); // 背景画像（差し替え可）

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(bg5, 0, 0, getWidth(), getHeight(), this);
		    }
		};
        container.add(boardPanel, "screen5");
        
        
        container.add(stay, "stay");
        
        add(container); // JFrameにコンテナを追加
        setVisible(true);
       


	}
	
	/***************************************************************mainメソッド****************************************************
	 *******************************************************************************************************************************/
	
	public static void main(String[] args){
		//名前入力画面とレベル選択画面
		//ClientGroup1 client = 
		address = "localhost";
		bangou = 10000;
		 new ClientGroup1("オセロポケット");
    
    }
	
	/***************************************************************送信関係プログラム****************************************************
	 *******************************************************************************************************************************/
	
	public void SendLogin(Player itsme,String msg){           //ログイン情報を送る
		if (out != null) {
		out.println("PLAYERNAME:" +String.valueOf(itsme.level)+"," +itsme.name+","+msg);
	    
	    out.flush(); // 全ての送信データをまとめて送る
	    System.out.println("サーバにプレイヤー情報を送信しました"); // テスト標準出力
		}else {
			System.err.println("送信できません: outがnull（サーバ未接続）");
	        cl.show(container, "error");  // GUIをエラー画面に切り替え
		}
	}
	
	
	
	public void SendHandhiYesorNO(String msg){           //ハンディキャップを下げるかどうかの選択を行う
		out.println("HCHECK:"+msg);    //基本DOWNかSTAY
	    
	    out.flush(); // 全ての送信データをまとめて送る
	    System.out.println("サーバにプレイヤー情報を送信しました"); // テスト標準出力
	}
	
	
	
	public void SendButtonXY(String msg){
		
		//押されたボタン情報を送信
		out.println("XY:"+msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにメッセージXY: " + msg + " を送信しました"); //テスト標準出力
		
	}
	
    public void SendPASS(){                  //サーバーにPASSを渡すメソッド
		
		//押されたボタン情報を送信
		out.println("PASS");//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにメッセージPASSを送信しました"); //テスト標準出力
		
	}
    
    public void SendSURRENDER(){                  //サーバーにPASSを渡すメソッド
		
		//押されたボタン情報を送信
		out.println("SURRENDER");//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバに降参を送信しました"); //テスト標準出力
		
	}
    
     public void SendRANDOM(){                  //サーバーにPASSを渡すメソッド
		
		//押されたボタン情報を送信
		out.println("SHUFFLE");//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバに降参を送信しました"); //テスト標準出力
		
	}
	
	/***************************************************************受信関係プログラム****************************************************
	 *******************************************************************************************************************************/
	public void HandhiZyusin(String msg){
		//ハンディキャップを受け取る
		System.out.println("サーバーからハンディキャップ番号を受け取りました: " + msg);
	}
	
	
	
	public void ReceiveXY(String msg) {
		//座標を受け取る
	    System.out.println("サーバーから座標を受け取りました: " + msg);
	}
	
	
	/***************************************************************Swing画面作成関係プログラム****************************************************
	 *******************************************************************************************************************************/
	
	
	public void setLevel(int level){
		//自分のプレイヤークラスのレベルをセット
        itsme.level=level;
        System.out.println("レベル："+level+"に設定しました");
    }

    public void setname(String name){
    	//自分のプレイヤーオブジェクトの名前をセット
        itsme.name=name;
        System.out.println("名前："+name+"に設定しました");
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//何のボタンが押されたかを検出する
		Object source = e.getSource();
		 // 切り替え処理
        
		
		if (source == b1) {                  //＜名前の画面の決定ボタン＞
			if(text1.getText().equals("")) {
				label.setText("名前を入力してください！");  //名前を表示
			}else {
				setname(text1.getText());       //nameに入力した名前を保存
				label.setText("プレイヤー名:"+text1.getText());  //名前を表示
				b2.setEnabled(true);
			}
	        
        } else if (source == b2) {           //＜次の画面へボタン＞
        	cl.show(container, "screen2");
        } else if (source == b3) {           //＜レベルの画面の決定ボタン＞
        	                                 //レベルが選択されたときの処理
            if(radio[0].isSelected()){
                setLevel(1);
            }else if(radio[1].isSelected()){
                
                setLevel(2);
            }else if(radio[2].isSelected()){
                
                setLevel(3);
            }else if(radio[3].isSelected()){
                
                setLevel(4);
            }else if(radio[4].isSelected()){
                
                setLevel(5);
            }else{
                System.out.println("レベルを選択してください");
            }
            
            b5.setEnabled(true);
            
        } else if (source == b4) {     //＜前の画面「名前選択」の決定ボタン＞
        	
        	cl.show(container, "screen1");
        	
        } else if (source == b5) {     //＜次の画面「ハンディキャップ確認画面」の決定ボタン＞
        	
         	cl.show(container, "screen2_1");
         	
        } else if (source == b6) {     //＜ハンディキャップこのままボタン＞
        	
        	flagb = 1;
        	SendHandhiYesorNO("STAY");    //このままでいいと送る
        	cl.show(container, "stay");    //待機画面に以降
        	
        }else if (source == b7) {      //＜ハンディキャップ一つ下げるボタン＞
        	
        	flagb = 0;
        	SendHandhiYesorNO("DOWN");     //下げてほしいと送る
        	cl.show(container, "stay");    //待機画面に以降
        	
        }else if (source == b6_1) {    //＜ハンディキャップこのままボタン＞
        	
        	flagb = 1;
        	SendHandhiYesorNO("STAY");    //このままでいいと送る
        	cl.show(container, "stay");    //待機画面に以降
        	
        }else if (source == b7_1) {     //＜ハンディキャップ一つ下げるボタン＞
        	
        	flagb = 0;
        	SendHandhiYesorNO("DOWN");     //下げてほしいと送る
        	cl.show(container, "stay");    //待機画面に以降
        	
        }else if (source == b8) {       //＜ハンディキャップ対戦を希望するボタン＞
        	
        	/********************サーバーに接続！！改善の余地あり********************/
        	if(ConnectCounter==0) {
        		connectServer(address,bangou);	
        	}                                                    ///////個々としたも変えよう
        	
        	cl.show(container, "stay");  //待機画面に以降
        	
         	try {
         	    Thread.sleep(500); // 1000ミリ秒（= 1秒）待つ
         	} catch (InterruptedException ev) {
         	    ev.printStackTrace();
         	}
         	
         	SendLogin(itsme,"yes");
         	
        }else if (source == b9) {        //＜ハンディキャップ対戦を希望しないボタン＞
        	
        	/********************サーバーに接続！！改善の余地あり********************/
        	if(ConnectCounter==0) {
        		connectServer(address,bangou);	
        	}                                                              ///////個々としたも変えよう
        	cl.show(container, "stay");  
        	
         	try {
         	    Thread.sleep(500); // 500ミリ秒（= 1秒）待つ
         	} catch (InterruptedException ev) {
         	    ev.printStackTrace();
         	}
         	
         	SendLogin(itsme,"no");
         	
         	flaga = 1;  //フラグを１にする（NOを選んだ)
        	
        }else if (source == b10) {        //＜対戦画面へ移行するボタン＞
        	
        	InitializeBoard(8);    //盤面を初期化する　
        	updateDisp(8);     //最初の盤面をcに描画する
        	
        	cl.show(container, "screen5");    //対戦画面に移行
        	container.revalidate();
            container.repaint();
            System.out.println("現在画面: screen5 (boardPanel)");
        }else if (source == b11) {        //＜最初の画面へ移行するボタン＞
        	
        	cl.show(container, "Start");  
        	
        	//ConnectCounter++;                                               //ここいる！？！？
        	disconnectServer();
        	
            System.out.println("エラー画面から最初の画面に戻りました！！！");
        }else if (source == bend) {        //＜ゲームを終了するボタン＞
        	
        	System.exit(0);                    //システムを終了するボタン？？？
            System.out.println("ゲームを終了しました");
        }else if (source == bagain) {        //＜最初の画面へ移行する+サーバーとの通信を切る＞
        	
        	ConnectCounter++;
        	SendRANDOM();
        	disconnectServer();
        	cl.show(container, "Start");  
        	
            System.out.println("対局終了画面から最初の画面に戻りました！！！");
           
        }
        
               //コンソール確認用
               System.out.println(itsme.name);
               System.out.println(itsme.level);
	}
	
	
	
	
	public void InitializeBoard(int row) { //引数はhandhi
		
		Arrays.fill(grids, "board");
		grids[27] = "black";
		grids[36] = "black";
		grids[28] = "white";
		grids[35] = "white";
		
		if(handhi==1) {                                                                  //ハンディキャップ番号によって変える1~4or0~3
			//1子局
			grids[0] = "black";
		}else if(handhi==2) {
			//2子局
			grids[0] = "black";
			grids[63] = "black";
		}else if(handhi==3) {
			//3子局
			grids[0] = "black";
			grids[63] = "black";
			grids[7] = "black";
		}else if(handhi==4) {
			//4子局
			grids[0] = "black";
			grids[63] = "black";
			grids[7] = "black";
			grids[56] = "black";
		}else {
			System.out.println("ハンディキャップなしで始めます...");
		}
		
		if(itsme.name.equals("DEBUG")) {                                           //盤面がでバックモードで起動される
			String []degrids = 
				{"black","white","black","black","black","black","black","board",
				"black","black","black","white","white","white","black","white",
				"black","black","black","white","white","white","black","white",
				"black","black","black","black","white","white","black","white",
				"black","black","black","white","white","black","black","white",
				"black","black","black","white","white","black","black","white",
				"black","black","black","white","white","black","black","white",
				"black","black","black","black","black","black","black","black"};
			
			for (int i = 0; i < 64; i++) {
			    grids[i] = degrids[i]; // 内容をコピー
			}
		}else if(itsme.name.equals("DEBUG1")) {                                           //盤面が白が詰みモードで起動される
			String []degrids = 
				{"white","white","white","white","white","black","black","white",
				 "white","white","white","white","black","black","white","white",
				 "white","white","white","white","black","white","white","board",
				 "black","white","board","black","white","white","board","board",
				 "white","white","black","white","white","board","board","board",
				 "black","black","white","white","board","board","board","board",
				 "white","black","white","board","board","board","board","board",
				 "black","black","white","board","board","board","board","board"};
			
			for (int i = 0; i < 64; i++) {
			    grids[i] = degrids[i]; // 内容をコピー
			}
		}else if(itsme.name.equals("DEBUG2")) {                                           //盤面が引き分けモードで起動される
			String []degrids = 
				{"white","white","black","black","white","white","black","board",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","white",
				 "white","white","black","black","white","white","black","black"};
			
			for (int i = 0; i < 64; i++) {
			    grids[i] = degrids[i]; // 内容をコピー
			}
		}else if(itsme.name.equals("DEBUG3")) {                                           //盤面が引き分けモードで起動される
			String []degrids = 
				{"black","black","black","black","board","board","black","board",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","white",
				 "black","black","black","black","board","board","black","black"};
			
			for (int i = 0; i < 64; i++) {
			    grids[i] = degrids[i]; // 内容をコピー
			}
		}
		
		
		//アイコン設定(画像ファイルをアイコンとして使う)
		whiteIcon = new ImageIcon(getClass().getResource("White.jpg"));      //白い駒
		blackIcon = new ImageIcon(getClass().getResource("Black.jpg"));      //黒い駒
		boardIcon = new ImageIcon(getClass().getResource("GreenFrame.jpg"));  //背面のボード
		PlaceIcon = new ImageIcon(getClass().getResource("PlaceFrame.jpg"));  //置ける場所のボード
		EdgeIcon  = new ImageIcon(getClass().getResource("EdgeFrame.jpg"));   //側面のやつ
		EdgeIcon_a = new ImageIcon(new ImageIcon(getClass().getResource("return_frame_a.jpg")).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
		EdgeIcon_b = new ImageIcon(new ImageIcon(getClass().getResource("return_frame_b.jpg")).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
		EdgeIcon_aa = new ImageIcon(new ImageIcon(getClass().getResource("return_frame_aa.jpg")).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
		EdgeIcon_bb = new ImageIcon(new ImageIcon(getClass().getResource("return_frame_bb.jpg")).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));

		
		Rainbow = new ImageIcon(getClass().getResource("Rain.png"));
		// boardPanel をクリア
        boardPanel.removeAll();
        
        if (itsme.BrackorWhite == 0) {//黒の時
        	
        	System.out.println("初期盤面を黒で始めます！！先手！！");
        	
        	textColor = "黒";             //個の文字列はこれで固定された
        	
        	textTeban = "あなたの手番です";
        	
        	text = "がんばれ！！";
        	
        	PlaceCheck(8,itsme.BrackorWhite);
        	
        }else if(itsme.BrackorWhite == 1) {  //白の時
        	
        	System.out.println("初期盤面を白で始めます！！後手！！");
        	
        	textColor = "白";     
        	
        	textTeban = "相手の手番です...";
        	
        	text = "がんばれ！！";
        	
        }else {
        	
        	System.out.println("値が不正です！！！");
        }
        
        //おふざけ機能追加場所
        
        if(itsme.name.equals("RainBow")) {
			OFUZAKEflag =1;
			textColor = "虹";
			if(itsme.BrackorWhite==0) {         //黒なら
				blackIcon = Rainbow;
			}else if(itsme.BrackorWhite==1) {    //白なら
				whiteIcon = Rainbow;
			}
			System.out.println("虹モードが起動しました");
		}
        
	}
	
public void WritingWinorLoseBoard(int row, JPanel BOARD,String[] grida) { //引数はhandhi
	 BOARD.removeAll(); 
	JLabel boarda[] = new JLabel[64];
		
		//アイコン設定(画像ファイルをアイコンとして使う)
	ImageIcon whiteIcon2 =  new ImageIcon(new ImageIcon(getClass().getResource("White.jpg")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));      //白い駒
	ImageIcon blackIcon2 =  new ImageIcon(new ImageIcon(getClass().getResource("Black.jpg")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));      //黒い駒
	ImageIcon boardIcon2 =  new ImageIcon(new ImageIcon(getClass().getResource("GreenFrame.jpg")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));  //背面のボード
        
        if(itsme.name.equals("RainBow")) {
			OFUZAKEflag =1;
			if(itsme.BrackorWhite==0) {         //黒なら
				blackIcon = Rainbow;
			}else if(itsme.BrackorWhite==1) {    //白なら
				whiteIcon = Rainbow;
			}
		}
        
        for(int i = 0 ; i < row * row ; i++){
 			if(grida[i].equals("black")||grida[i].equals("edge_a")||grida[i].equals("edge_aa")){
 				boarda[i] = new JLabel(blackIcon2);
 				boarda[i].setAlignmentX(Component.CENTER_ALIGNMENT);
 				}//盤面状態に応じたアイコンを設定
 			if(grida[i].equals("white")||grida[i].equals("edge_b")||grida[i].equals("edge_bb")){
 				boarda[i] = new JLabel(whiteIcon2);
 				boarda[i].setAlignmentX(Component.CENTER_ALIGNMENT);
 				}//盤面状態に応じたアイコンを設定
 			if(grida[i].equals("board")||grida[i].equals("place")){
 				boarda[i] = new JLabel(boardIcon2);
 				boarda[i].setAlignmentX(Component.CENTER_ALIGNMENT);
 				}//盤面状態に応じたアイコンを設定
 			
 			BOARD.add(boarda[i]);//ボタンの配列をペインに貼り付け
 			// ボタンを配置する
 			int x = (i % row) * 25;
 			int y = (int) (i / row) * 25;
 			boarda[i].setBounds(x, y, 25, 25);//ボタンの大きさと位置を設定する．
 		}
        
	}
	
	
	/***************************************************************対戦画面関係プログラム****************************************************
	 *******************************************************************************************************************************/
	
	
	public synchronized void updateDisp(int row) {      //画面を更新するプログラムString [] gridsmemoryは変える前の盤面情報を保持している
		
		boardPanel.removeAll();
		boardPanel.revalidate();
		boardPanel.repaint();
		//オセロ盤の生成
         buttonArray = new JButton[row * row];//ボタンの配列を作成
         int blacknum=0;
         int whitenum=0;
        
         for(int i = 0 ; i < row * row ; i++){
 			if(grids[i].equals("black")){ buttonArray[i] = new JButton(blackIcon);blacknum++;}  //盤面状態に応じたアイコンを設定
 			if(grids[i].equals("white")){ buttonArray[i] = new JButton(whiteIcon);whitenum++;}  //盤面状態に応じたアイコンを設定
 			if(grids[i].equals("board")){ buttonArray[i] = new JButton(boardIcon);}             //盤面状態に応じたアイコンを設定
 			if(grids[i].equals("place")){ buttonArray[i] = new JButton(PlaceIcon);}             //盤面状態に応じたアイコンを設定
 			if(grids[i].equals("edge_a")){ buttonArray[i] = new JButton(EdgeIcon_a);whitenum++;}//盤面状態に応じたアイコンを設定
 			if(grids[i].equals("edge_b")){ buttonArray[i] = new JButton(EdgeIcon_b);blacknum++;}//盤面状態に応じたアイコンを設定
 			if(grids[i].equals("edge_aa")){ buttonArray[i] = new JButton(EdgeIcon_aa);whitenum++;}//盤面状態に応じたアイコンを設定
 			if(grids[i].equals("edge_bb")){ buttonArray[i] = new JButton(EdgeIcon_bb);blacknum++;}//盤面状態に応じたアイコンを設定
 			if (grids[i] == null) {
 			    System.out.println("grids[" + i + "] is null");
 			}
 			boardPanel.add(buttonArray[i]);//ボタンの配列をペインに貼り付け
 			// ボタンを配置する
 			int x = (i % row) * 45;
 			int y = (int) (i / row) * 45;
 			buttonArray[i].setBounds(x, y+30, 45, 45);//ボタンの大きさと位置を設定する．
 			buttonArray[i].addMouseListener(this);//マウス操作を認識できるようにする
 			buttonArray[i].setActionCommand(Integer.toString(i));//ボタンを識別するための名前(番号)を付加する
 		}
         
         //スコアラベル
         if(itsme.BrackorWhite==0) {  //黒だった時、最初を黒
        	 textnow="黒"+blacknum+"　ー　"+"白"+whitenum;
         }else {                      //白だった時、最初を白
        	 textnow="白"+whitenum+"　ー　"+"黒"+blacknum;
         }
            
            NowScore = new JLabel(textnow);
			NowScore.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 19));
			NowScore.setBounds(0, row * 45 + 30, (row * 45 + 10) / 2, 30);//スコアラベルの境界を設定
			boardPanel.add(NowScore);//手番情報ラベルをペインに貼り付け
			
			//降参ボタン
			pass = new JButton("降参する");//降参ボタンを作成
			boardPanel.add(pass); //パスボタンをペインに貼り付け
			pass.setBounds((row * 45 + 10) / 2, row * 45 + 30, (row * 45 + 10 ) / 2, 30);//パスボタンの境界を設定
			pass.addMouseListener(this);//マウス操作を認識できるようにする
			pass.setActionCommand("pass");//ボタンを識別するための名前を付加する
				
			//色表示用ラベル
			colorLabel = new JLabel("Your Color is ... "+textColor+"!!");//色情報を表示するためのラベルを作成
			colorLabel.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 19));
			colorLabel.setBounds(10,0 , row * 45 + 10, 30);//境界を設定
			boardPanel.add(colorLabel);//色表示用ラベルをペインに貼り付け
				
			//手番表示用ラベル
			turnLabel = new JLabel(textTeban);//手番情報を表示するためのラベルを作成
			turnLabel.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 19));
			turnLabel.setBounds(10, row * 45 + 90, row * 45 + 10, 30);//境界を設定
			boardPanel.add(turnLabel);//手番情報ラベルをペインに貼り付け
				
			//手番表示用ラベル
			messageLabel = new JLabel(text);//手番情報を表示するためのラベルを作成
			messageLabel.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 18));
			messageLabel.setBounds(10, row * 45 + 150, row * 45 + 10, 30);//境界を設定
			messageLabel.setForeground(Color.RED);     //赤にする
			boardPanel.add(messageLabel);//手番情報ラベルをペインに貼り付け
				
				
	}
	/***************************************************************アニメーション関係プログラム****************************************************
	 *******************************************************************************************************************************/
	
public  void updateDispAnimation(int row,String [] gridsmemory,int place) {      //画面を更新するプログラムアニメーション版String [] gridsmemoryは変える前の盤面情報を保持している
		
	
	animationInProgress = true; // アニメーション開始！
	
		List<Integer> changedIndexes = new ArrayList<>();
		String[] actualNewColor = new String[row * row];  // 実際の新しい色を記録

		// 変化したマスのインデックスを記録
		for (int o = 0; o < row * row; o++) {
			if (!gridsmemory[o].equals(grids[o]) && o != place && !gridsmemory[o].equals("place")) { //自分がおいたor相手がおいたマスはアニメーションをしない
				changedIndexes.add(o);
				actualNewColor[o] = grids[o];
			}
		}

		// アニメーション用のステップ
		final int[] step = {0};
		final Timer timer = new Timer(200, null); // 200msごとに処理

		timer.addActionListener(e -> {
			if (step[0] == 0) {
				// ステップ1：変化する場所を"edge"に変えて表示
				for (int i : changedIndexes) {
					if(grids[i].equals("black")) {   //returnframea
					   grids[i] = "edge_b";
					}else {                          //returnframeb
						grids[i] = "edge_a";
					}
				}
				updateDisp(row);
				cl.show(container, "screen5");
				step[0]++;
			}  else if (step[0] == 1) {
	            // ステップ1：中間エフェクト（edge2）
	            for (int i : changedIndexes) {
	                if (actualNewColor[i].equals("black")) {
	                    grids[i] = "edge_bb";  // 2段階目
	                } else {
	                    grids[i] = "edge_aa";  // edge2 for white
	                }
	            }
	            updateDisp(row);
	            cl.show(container, "screen5");
	            step[0]++;
	        }else if (step[0] == 2) {
				// ステップ2：もとの色に戻して表示
				for (int i : changedIndexes) {
					
					grids[i] = actualNewColor[i];
            }
			if(placeflag==true) {
			 PlaceCheck(8,itsme.BrackorWhite);   //置ける場所を示す
			}
            updateDisp(row);
            PermitOperation(8,permit);
            cl.show(container, "screen5");
            timer.stop(); // アニメーション終了
            animationInProgress = false;
	        processPendingMessages();  // キューにたまってたメッセージを処理！
        }
    });

    timer.setInitialDelay(0); // 最初の実行をすぐに
    timer.start();
				
	}

    private void processPendingMessages() {
      synchronized (this) {
        while (!messageQueue.isEmpty()) {
            String msg = messageQueue.poll(); // キューから取り出す
            receiveMessage(msg); // 再帰的に処理する
        }
        }
      }

    /***************************************************************アニメーション関係プログラム****************************************************
	 *******************************************************************************************************************************/


	public void PlaceCheck(int row,int color) {   //置けるところを示す
		
		String[] rid = grids.clone(); //最初の置く場所を保存しておく
		
		for(int i=0;i<64;i++) {  //すべてのマスに対して試す
			
			// 安全のため毎回 clone して渡す
			String[] testGrid = rid.clone();  //さらにクローンする
	        String[] result = othello.putStone(testGrid, i, color);

	        // 有効な手なら "-1" ではない
	        if (!result[0].equals("-1")) {   //置けるなら
	        	grids[i] = "place";
	        }
		}
		
	}
	
public void DeletePlace(int row) {     //置けるやつを消す
		
		for(int i=0;i<64;i++) {  //すべてのマスに対して試す
			
			if(grids[i].equals("place")) {                  //そのマスに置けたらそこの文字列を"place"にする置ける場所
				grids[i] = "board";
			}
			
		}
	}

   
	
	public synchronized void PermitOperation(int row, boolean x){	// プレイヤの操作を許すか許さないかを  これいる？
		
		//for(int i = 0 ; i < row * row ; i++){                   //先手の場合、すべてのボタンへのアクセスを許可する
    		//buttonArray[i].setEnabled(x);                    //後手の場合、すべてのボタンへのアクセスを拒否する
    	//}
	}
	
	//マウスクリック時の処理
		public void mouseClicked(MouseEvent e) {
			JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．キャストを忘れずに
			String command = theButton.getActionCommand();//ボタンの名前を取り出す
			Object src = e.getComponent();  //オブジェクトを取り出す
			System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");//テスト用に標準出力
			
			if(command.equals("pass")) {             //降参ボタンがクリックされたとき
				
				Surrender = true;
				SendSURRENDER();                   //サーバーに降参を送る
				
			}else{               
				int xy = Integer.parseInt(command);            //押されたボタンをもとにxyに格納
				
				String [] gridsmemory = new String[64];          //変更前の配列を覚えておく下も
				for (int i = 0; i < 64; i++) {
				    gridsmemory[i] = grids[i]; // 内容をコピー
				}
				
				
				DeletePlace(8);
				grids=othello.putStone(grids,xy,itsme.BrackorWhite);    //置く
				
				
				
				if(grids[0].equals("-1") && permit==true) {
					
					text = "そこには置けないぞ...(´;ω;｀)";
					System.out.println("置けない場所に置かれました...");
					for (int i = 0; i < 64; i++) {
					    grids[i] = gridsmemory[i]; // 元の内容をコピー
					}
					updateDisp(8);     //cに描画する
					cl.show(container, "screen5");    //再描画する
					System.out.println("対戦画面に行きました4");
					
				}else if (!grids[0].equals("-1") && permit==true) {
					Random rand = new Random();
			        int num = rand.nextInt(4) + 1;      // 1〜4
					 SendButtonXY(command);               //相手にメッセージを送信
					 if(num==1) {
						 text = "めっちゃいい手じゃん！";
					 }else if(num==2) {
						 text = "やるじゃん！";
					 }else if(num==3) {
						 text = "ええやん！";
					 }else {
						 text = "天才じゃん！";
					 }
				     textTeban = "相手の手番です...";      //ラベルを相手の手番にする
				     
					 
				     permit = false;     
				     
				    placeflag=false;
				     updateDispAnimation(8,gridsmemory,xy);      //アニメーションで盤面を変える
				     System.out.println("対戦画面に行きました5");
				     
				}else if(permit==false) {
					text ="お前の手番ではないぞ...(￣ー￣)";
					System.out.println("置けない場所に置かれました...");
					for (int i = 0; i < 64; i++) {
					    grids[i] = gridsmemory[i]; // 内容をコピー
					}
					updateDisp(8);     //cに描画する
					PermitOperation(8,permit);    
					cl.show(container, "screen5");    //再描画する
					System.out.println("対戦画面に行きました6");
				}
				 
			}
			   
			
			
		}
		
		public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
		public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
		public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
		public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理
		
	
	/***************************************************************サーバー通信関係プログラム****************************************************
	 *******************************************************************************************************************************/
	
	public void connectServer(String ipAddress, int port){	// サーバに接続
		socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); //座標データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備
			receiver.start();//受信用オブジェクト(スレッド)起動
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			 this.out = null;
		     this.receiver = null;
			cl.show(container, "error");    //エラー画面に移行
			//System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			 this.out = null;
		     this.receiver = null;
			cl.show(container, "error");    //エラーが面に移行
			//System.exit(-1);
		}
	}
	
	public void disconnectServer()  {                  //サーバーとの通信を切っていろいろリセットするメソッド
	    
		b2.setEnabled(false);
		b5.setEnabled(false);
	        /*********************すべてのフラグをリセットする************************/
	        handhi=0;                     //ハンディキャップ用の変数 0-4
	    	handhiKIOKU = 0;            //一回目のハンディを記憶しておくよう
	    	xy=0;                     //座標を格納する変数
	    	HANDIcounter = 0;                      //ハンディキャップを何回受け取るかを示す
	    	flag_11=0;
	    	flaga = 0;
	    	flagb = 0;
	    	permit = false;
	    	OFUZAKEflag = 0;
	    	alreadySentEnd = false; // クラスのメンバ変数として宣言しておく
	    	ENDGAME = false;              //一回終了画面になったかどうか
	    	Surrender = false;
	    	animationInProgress = false;            //アニメーションをしない
	    	placeflag = false;            //アニメーションをしない
	        
	    	text="";
	    	label10.setText(text);
	    	textColor="";
	    	textTeban="";
	    	textwinorlose = "";
	        textscore = "";
	    	textnow="";
	   
	}
	
	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
				cl.show(container, "error");    //エラーが面に移行
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {//データを受信し続ける
					String inputLine = br.readLine();//受信データを一行分読み込む
					if (inputLine != null){//データを受信したら
						receiveMessage(inputLine);//データ受信用メソッドを呼び出す
					}
				}
			} catch (IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
				cl.show(container, "error");    //エラーが面に移行
			}
		}
	}
		public synchronized void receiveMessage(String msg){	// メッセージの受信
			if (animationInProgress) {
		        messageQueue.add(msg); // アニメーション中ならキューに保留
		        System.out.println("キューに保存しました");
		        return;
		    }
			System.out.println("サーバからメッセージ " + msg + " を受信しました"); //テスト用標準出力

			       // ハンディキャップのメッセージなら HandhiZyusin() に渡す
			if (msg.startsWith("HANDI:")) {              //ハンディキャップ番号をおくられたとき HANDI:3,BLACK
				
				String[] parts = msg.split(":");// "HANDI:" の後ろの部分だけを抽出

				    // 「3,BLACK」をさらに「,」で分割 → ["3", "BLACK"]
				String[] values = parts[1].split(",");
				
				handhi =Integer.parseInt(values[0]);         //int型に変換してhandhiに格納
				
				if(values[1].equals("BLACK")) {
					itsme.BrackorWhite = 0;                  //作成したPlayerクラスのオブジェクトに数字を入れる
					System.out.println("黒になりました！");
				}else if(values[1].equals("WHITE")){
				 itsme.BrackorWhite = 1;                 //作成したPlayerクラスのオブジェクトに数字を入れる
				 System.out.println("白になりました！");
				}else {
			       System.out.println("受け取った先手後手情報が不正です");
				}
				
				HandhiZyusin(values[0]);
				
				
				if(HANDIcounter==0) {   //最初なら
				    HANDIcounter++;
				    
                    /////////////////////////////画面切り替え処理////////////////////////////////////////////////
				    HANDIcounter++;
				    handhiKIOKU = handhi;          //一回目を記憶しておく

		            SwingUtilities.invokeLater(() -> {
		                if (handhi == 0) { //ハンディキャップがないとき
	                    	ImageIcon newIcon = new ImageIcon(getClass().getResource("handhi0.png"));
	                    	Image img = newIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
	                    	imageLabel3.setIcon(new ImageIcon(img));
	                    	imageLabel3.revalidate(); // 念のため
	                    	imageLabel3.repaint();    // 念のため
	                    	if (flaga == 0) {//ハンディキャップ対戦を希望しているYESを選んだとき
		                        text = "<html>すみません、諸事情により、<br>ハンディキャップ無し対戦になりました</html>";
		                        label10.setText(text);
		                    }
		                    cl.show(container, "screen6");                  //改善が必要？？？かも？？？
		                    Timer timer = new Timer(5000, new ActionListener() {
					            @Override
					            public void actionPerformed(ActionEvent e) {
					            	InitializeBoard(8);    //盤面を初期化する　
					            	
					            	if(itsme.BrackorWhite==0) {
					            		permit = true;     //黒の時、手番を受け付ける
					            	}else {
					            		permit = false;    //白の時、手番を受け付けない
					            	}
					            	updateDisp(8);     //最初の盤面をcに描画する
					            	PermitOperation(8,permit);    //黒が先手だから操作を受け付け
					            	
					            	cl.show(container, "screen5");    //対戦画面に移行
					            	container.revalidate();
					                container.repaint();
					                System.out.println("現在画面: screen5 (boardPanel)");
					            }
					        });
					        timer.setRepeats(false);
					        timer.start();
		                } else { //対戦画面に移行
		                    if (flaga == 1) {//NOを選んでるとき
		                        text = "<html>相手の申し出により、<br>ハンディキャップ対戦となりました</html>";
		                    } else if (flaga == 0) {//YESを選んでるとき
		                        text = "";
		                    }
		                    
		                    String path;
	                    	switch(handhi) {
	                    		case 0: path = "handhi0.png"; break;
	                    		case 1: path = "handhi1.png"; break;
	                    		case 2: path = "handhi2.png"; break;
	                    		case 3: path = "handhi3.png"; break;
	                    		case 4: path = "handhi4.png"; break;
	                    		default: path = "Black.jpg"; break;
	                    	}

		                    if (itsme.BrackorWhite == 0) {//黒の時
		                    	label8.setText(text);
		                    	ImageIcon newIcon = new ImageIcon(getClass().getResource(path));
		                    	Image img = newIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		                    	imageLabel.setIcon(new ImageIcon(img));
		                    	imageLabel.revalidate(); // 念のため
		                    	imageLabel.repaint();    // 念のため
		                        cl.show(container, "screen3");//黒の確認画面
		                    } else if (itsme.BrackorWhite == 1) {//白の時
		                    	label7.setText(text);
		                    	ImageIcon newIcon = new ImageIcon(getClass().getResource(path));
		                    	Image img = newIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		                    	imageLabel2.setIcon(new ImageIcon(img));
		                    	imageLabel2.revalidate(); // 念のため
		                    	imageLabel2.repaint();    // 念のため
		                        cl.show(container, "screen4");//白の確認画面
		                    } else {
		                        System.out.println("値が不正です");
		                    }
		                }
		            });
				    
				    
				}else {//２回目以降なら
					System.out.println("2回目に行きました");
				
					/////////////////////////////画面切り替え処理を入れる////////////////////////////////////////////////
					SwingUtilities.invokeLater(() -> {   //最終確認画面
						String path;
                    	switch(handhi) {
                    		case 0: path = "handhi0.png"; break;
                    		case 1: path = "handhi1.png"; break;
                    		case 2: path = "handhi2.png"; break;
                    		case 3: path = "handhi3.png"; break;
                    		case 4: path = "handhi4.png"; break;
                    		default: path = "Black.jpg"; break;
                    	}
                    	if(handhi < handhiKIOKU  && flagb==1 ) {
	                    	
	                        if(handhi==0) {
	                        	text = "<html>相手の申し出により、<br>ハンディキャップ無し対戦となりました</html>";
		                        label10.setText(text);
	                        }else {
	                        	text = "<html>相手の申し出により、<br>ハンディキャップが一つ下がりました</html>";
		                        label10.setText(text);
	                        }
	                    }else if(handhi == handhiKIOKU && flagb==0) {  //
	                    	text = "<html>相手の申し出により、<br>ハンディキャップは変更されませんでした。</html>";
	                        label10.setText(text);
	                    }
                    	ImageIcon newIcon = new ImageIcon(getClass().getResource(path));
                    	Image img = newIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    	imageLabel3.setIcon(new ImageIcon(img));
                    	imageLabel3.revalidate(); // 念のため
                    	imageLabel3.repaint();    // 念のため
					cl.show(container, "screen6");
					
					Timer timer = new Timer(5000, new ActionListener() {
			            @Override
			            public void actionPerformed(ActionEvent e) {
			            	InitializeBoard(8);    //盤面を初期化する　
			            	updateDisp(8);     //最初の盤面をcに描画する
			            	
			            	if(itsme.BrackorWhite==0) {   //黒なら先手
			            		permit = true;   //最初に置けるようにする
			            	}else {
			            		permit = false;  //白なら後手
			            	}
			            	
			            	PermitOperation(8,permit);    //黒が先手だから操作を受け付け
			            	
			            	cl.show(container, "screen5");    //対戦画面に移行
			            	container.revalidate();
			                container.repaint();
			                System.out.println("現在画面: screen5 (boardPanel)");
			            }
			        });
			        timer.setRepeats(false);
			        timer.start();
			    
					});		
					
				}
				
				
				
			}else if (msg.startsWith("XY:")) {
				
				
				//相手からのアクションがきた盤面を出力するブロック
					String newmsg = msg.substring(3); // "XY:" の後ろの部分だけを抽出
					ReceiveXY(newmsg);
					String [] gridsmemory = new String[64];          //変更前の配列を覚えておく下も
					
				    xy =Integer.parseInt(newmsg);  //int型に変換してXYに格納
				    
					for (int i = 0; i < 64; i++) {
					    gridsmemory[i] = grids[i]; // 内容をコピー
					}
					
					if(itsme.BrackorWhite==1) {         //自分が1なら
						grids=othello.putStone(grids,xy,0);
					}else {                            //自分が0なら
						grids=othello.putStone(grids,xy,1);
					}
					
					
              
					flag_11 =1;                    //フラグを１にする
					     
				
				String [] gridsmemory2 = new String[64];          //変更前の配列を覚えておく下も
				
				for (int i = 0; i < 64; i++) {
				    gridsmemory2[i] = grids[i]; // 内容をコピー
				}
				
				if(othello.isGameover(grids)==1 && !alreadySentEnd) { //ゲームオーバーなら
					 SendPASS();                          //サーバーにPASSを渡す
					 alreadySentEnd = true; // これ以上送らないようにする
					 permit=false;
					 textTeban = "ゲームが終了しました...";
				     text = "結果表示画面に移行します...";
				     flag_11 =2;
				     
				}else if(othello.checkMate(gridsmemory2,itsme.BrackorWhite)==0) {   //詰んでる場合、
					 SendPASS();                          //サーバーにPASSを渡す
					 permit=false;
					 textTeban = "相手の手番です";
				     text = "置ける場所がなかったので相手にPASS!";
				     flag_11 =2;
				     
				}
				
				SwingUtilities.invokeLater(() -> {
					
					if(flag_11==0) {
						PlaceCheck(8,itsme.BrackorWhite);
						updateDisp(8);     //cに描画する  PASSされたら盤面はそのまま帰って来るのでそのまま描画する
						PermitOperation(8,permit);    //黒が先手だから操作を受け付け 
				        cl.show(container, "screen5");    //再描画する
				     //ここ必要かな？
				        System.out.println("対戦画面に行きました1");
					}else if(flag_11==1) {
						 
						 permit=true;
						 textTeban = "あなたの手番です";
						 text = "がんばれ！";
					     placeflag = true;
					     updateDispAnimation(8,gridsmemory,xy);   //アニメーションの処理
					     System.out.println("対戦画面に行きました2");
					}else {
						
						updateDisp(8);     //cに描画する  PASSされたら盤面はそのまま帰って来るのでそのまま描画する
						 PermitOperation(8,permit);    //黒が先手だから操作を受け付け
						 
				         cl.show(container, "screen5");    //再描画する
				         System.out.println("対戦画面に行きました3");
					}
					
				});	
				
				
				 
			}else if (msg.startsWith("PLAYER:")) {
				System.out.println(msg);
				String newmsg = msg.substring(7); 
				String[] parts =newmsg.split(",");
				
				enemy.name =parts[1];   //相手の名前をenemyのオブジェクトに格納
				enemy.level = Integer.parseInt(parts[0]); //相手のレベルをenemyのオブジェクトに格納
				
				ReceiveXY(newmsg);
				
			}else if (msg.startsWith("FINISH:") ) {    //FINISHが来たとき
				
				String newmsg = msg.substring(7); // "FINISH:" の後ろの部分だけを抽出
				
				//相手からの接続が切れたとき
				if(newmsg.equals("DISCONNECT")&& ENDGAME == false) {    //接続が切れたフラグが来たとき
					//黒か白かボードが亡くなった時
					 textwinorlose = "YOU WIN!!";
					 textscore = "相手の接続が切れました...";
					
					 
					 // 1秒待ってからEND画面を表示する
					 SwingUtilities.invokeLater(() -> {
						 WritingWinorLoseBoard(8,BOARD,grids);
						 BOARD.repaint();
					            labelwin.setText(textwinorlose);
					            labelscore.setText(textscore);
					            labelwin.revalidate(); // 念のため
					            labelwin.repaint();    // 念のため
					            labelscore.revalidate();
					            labelscore.repaint();
					            cl.show(container, "END"); // 画面遷移

					       
					    });
					   ENDGAME = true;
				}else if(newmsg.equals("END")&& ENDGAME == false) {                  //正常に終了したとき
					
					SwingUtilities.invokeLater(() -> {
						
					int numBlack = 0;
					int numWhite = 0;
					for(int i = 0;i<64;i++) {                   //石の数を数える
						if(grids[i].equals("black")) {
							numBlack++;
						}
						
                        if(grids[i].equals("white")) {
							numWhite++;
						}
					}
					
					String msg3;
					
					if(handhi==0) {            //ハンディキャップがない場合
						msg3 = othello.checkWinner(grids, 0);
					}else {                //ハンディキャップがある場合
						msg3 = othello.checkWinner(grids, 1);
					}
					
					
					if(msg3.equals("black") && itsme.BrackorWhite == 0) {        //勝者が黒、自分が黒
						textwinorlose = "YOU WIN!!";
						textscore = "黒"+numBlack+"　ー　"+"白"+numWhite;
						labelwin.setForeground(Color.RED); // 勝ち：赤
					
					}else if(msg3.equals("black") && itsme.BrackorWhite == 1) {    //勝者が黒、自分が白
						textwinorlose = "YOU LOSE...";
						textscore = "白"+numWhite+"　ー　"+"黒"+numBlack;
						labelwin.setForeground(Color.BLUE); // 勝ち：赤
					}else if(msg3.equals("white") && itsme.BrackorWhite == 0) {       //勝者が白、自分が黒
						textwinorlose = "YOU LOSE...";
						textscore = "黒"+numBlack+"　ー　"+"白"+numWhite;
						labelwin.setForeground(Color.BLUE); // 勝ち：赤
					}else if(msg3.equals("white") && itsme.BrackorWhite == 1) {         //勝者が白、自分が白
						textwinorlose = "YOU WIN!!";
						textscore = "白"+numWhite+"　ー　"+"黒"+numBlack;
						labelwin.setForeground(Color.RED); // 勝ち：赤
					}else  {         //勝者が白、自分が白
						textwinorlose = "DRAW!!";
						textscore = "白"+numWhite+"　ー　"+"黒"+numBlack;
						labelwin.setForeground(Color.GREEN); // 勝ち：赤
					}
					
					//Timer timer = new Timer(500, new ActionListener() {
				        //public void actionPerformed(ActionEvent evt) {
					labelwin.setText(textwinorlose);
		            labelscore.setText(textscore);
		            WritingWinorLoseBoard(8,BOARD,grids);
		            labelwin.revalidate(); // 念のため
		            labelwin.repaint();    // 念のため
		            labelscore.revalidate();
		            labelscore.repaint();
		            BOARD.repaint();
		            container.revalidate();
	                container.repaint();
	                cl.show(container, "END"); // 画面遷移

		            // タイマーは一度しか使わないので停止
		            //((Timer) evt.getSource()).stop();
		        //}
		   // });
		    //timer.setRepeats(false); // 一回だけ実行
		   // timer.start();
	                
					});	
					ENDGAME = true;
		     }
				
				 
			}else if (msg.startsWith("PASS")) {    //PASS通知を受け取った時
                String [] gridsmemory2 = new String[64];          //変更前の配列を覚えておく下も
				
				for (int i = 0; i < 64; i++) {
				    gridsmemory2[i] = grids[i]; // 内容をコピー
				}
				
				if((othello.isGameover(grids)==1&& !alreadySentEnd)||othello.checkMate(gridsmemory2,itsme.BrackorWhite)==0) {  //盤面埋まっているかつ,SendPASSをまだしていない、または盤面に置けないとき
				permit=false;                         //置けなくする
				SendPASS();                          //サーバーにPASSを渡す
				 textTeban = "ゲームが終了しました...";
			     text = "結果表示画面に移行します...";
			     alreadySentEnd = true;              // これ以上送らないようにする
			     
				}else {                          //相手からただパスをされたとき
					
					 permit=true;                    //置けるようにする
					 textTeban = "あなたの手番です";
					 text = "相手からPASSされました...";
				     PlaceCheck(8,itsme.BrackorWhite);
					 updateDisp(8);     //cに描画する  PASSされたら盤面はそのまま(さっきマウスで置いた盤面)帰って来るのでそのまま描画する
					 PermitOperation(8,permit);    //操作を受け付け
					 
			         cl.show(container, "screen5");    //再描画する
				} 
				
			}else if (msg.startsWith("SURRENDER")) {    //PASS通知を受け取った時
				//黒か白かボードが亡くなった時
		    	 if(Surrender==true) {//自分が降参したとき
		    		 textwinorlose = "YOU LOSE!!";
					  textscore = "降参しました..."; 
					  labelwin.setForeground(Color.BLUE); // 負
		    	 }else {
		    		 textwinorlose = "YOU WIN!!";
					  textscore = "相手が降参しました..."; 
					  labelwin.setForeground(Color.RED); // 勝
		    	 }
				 
				 // 1秒待ってからEND画面を表示する
				 SwingUtilities.invokeLater(() -> {
					 WritingWinorLoseBoard(8,BOARD,grids);
					 BOARD.repaint();
				            labelwin.setText(textwinorlose);
				            labelscore.setText(textscore);
				            labelwin.revalidate(); // 念のため
				            labelwin.repaint();    // 念のため
				            labelscore.revalidate();
				            labelscore.repaint();
				            cl.show(container, "END"); // 画面遷移

				       
				    });
				   ENDGAME = true;
			}
			
		}
		
		// 丸いボタンのクラス（内部クラスでもOK！）
		class RoundButton extends JButton {
		    public RoundButton(String label) {
		        super(label);
		        setContentAreaFilled(false);
		        setFocusPainted(false);
		        setBorderPainted(false);
		        setForeground(Color.WHITE);
		        setFont(new Font("MS ゴシック", Font.BOLD, 12));
		    }

		    @Override
		    protected void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D) g.create();
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        // 塗りつぶし（半透明の黒）
		        g2.setColor(new Color(0, 0, 0, 180));
		        g2.fillOval(0, 0, getWidth(), getHeight());

		        // テキスト描画
		        FontMetrics fm = g2.getFontMetrics();
		        int x = (getWidth() - fm.stringWidth(getText())) / 2;
		        int y = (getHeight() + fm.getAscent()) / 2 - 2;
		        g2.setColor(getForeground());
		        g2.drawString(getText(), x, y);

		        g2.dispose();
		    }

		    @Override
		    public Dimension getPreferredSize() {
		        return new Dimension(50, 50); // 丸のサイズ
		    }

		    @Override
		    public boolean contains(int x, int y) {
		        double dx = x - getWidth() / 2;
		        double dy = y - getHeight() / 2;
		        return dx * dx + dy * dy <= (getWidth() / 2) * (getWidth() / 2);
		    }
		}

}

/*　　　　　　　　　　　　　　開発記録
 * 
 * 4/28
 * さっぱりわからないCHATGPTにはたよらない
 * とりあえず使いそうなクラス名のみ書き出してみた
 * 
 *5/1のまとめ 
 *大体できた？？？
 *怪しいのはオセロ画面の更新関係がうまくいくかどうか
 * 
 *アイデア、Jbuttonのenable,disenableを使ってエラーを置きなくさせる 
 *
 * 次にやること↓
 * オセロクラスができるまで待つ。バトル開始のやつをボタン依存ではなくて時間経過などにする？？？
 * 細かいところの修正。
 * CHATGPTにこれで再描画できているかどうかを聞く。
 * 
 * もう置けないとわかった時、オセロクラスが合図をしてくれる。→パス合図をサーバーに送る（パスボタンでも贈られる）
 * 終了受け取りもする→ 受け取ったらcl.showsで終わりの画面に
 * 
 * 5/5
 * レベル差がなかった時、ハンディキャップなしの対戦になるのでその注意書きをした方がいい？？
 * スレッドアニメーション
 * もう一度、チームの総意とあっているかどうかを見てみる
 * サーバーと接続が切れたとき、繋げなかった時どうすればいいか
 * 
 * 最終確認画面についてボタンで対戦画面に移行→時間経過にした方が安全かもしれない
 * 
 * 5/6
 * 
 * オセロクラスの導入により、盤面をひっくり返すところまで処理が終了した！
 * 
 * 置けなかった時にもう一回自分の手番をできるようにしたい！
 * 
 * 別のPCとの接続がうまくできないため、がんばる。ファイアウォール？？
 * 
 * 5/7
 * 
 * レベル低いほうがハンディキャップを下げる（HCHECK:DOWN）ことにしたとき、お互い待機中の画面のまま動かなくなるバグ発見
 * どっちも下げる(HCHECK:DOWN)とどっちもそのまま(HCHECK:STAY)とレベル高いほうがハンディキャップを下げる→レベルが高いほうから低いほうに(XY:)が送られない
 * 
 * 属性として、今自分の手番かどうかを表すboolean型変数permit導入により、相手の手番で駒がおけないようにした。
 * 
 * ～追加のおふざけポイント～
 * エモート　→　三種類くらいのボタン？？
 * 駒スキン替え　→　自分の画面だけ？？
 * 　　　　　　　　→　せっかくのレベルなのでレベルごとに違うスキンにする？？
 * 　　　　　　　　→要求仕様に合わない可能性が...
 * チャット機能　→　相手にメッセージを送れる。暴言ロック機能付き。
 * GODモード　→　名前を"GOD"にして起動？？？
 * Tips　→　接続待機中にオセロのルール説明が入る
 * 
 * OFUZAKE:
 * PICTURE:
 * 
 * 5/8
 * プレイヤー名を"RainBow"でログインすると、駒が虹色になる裏機能を実装。
 * 機能見つけたバグを解決！サーバー側のwhileのスピンロックっぽい？？
 * アニメーション機能の実装が完了！！　　結構大変だったから通信に負荷がかかるかも？？一応
 * updatedispの根本的改善？軽くする？
 * 
 * 5/9
 * ゲームが終わる仕組みを入れたらめっちゃばくっだでも修正完了！
 * 盤面が埋まったら終了画面に行くようにできた！！
 * 次はPASSからPASSが続いた時の終了と区別する
 * PASSを送ったらあいてもPASSするようになってる
 * 1626行目を確認 
 * 
 * 5/14
 * 1763行インヴォークれいたー？？
 * 勝ち負け引き分けの動作確認
 * キューによってアニメーション注に来たメッセージを後から実行させることに成功
 * このメソッドってreceiver.つけるのかな？
 * 今はリセットメソッドを作ってる最中
 * */
