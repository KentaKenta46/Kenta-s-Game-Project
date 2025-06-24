
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerSample2{
	private int port; // サーバの待ち受けポート
	private boolean [] online; //オンライン状態管理用配列
	private PrintWriter [] out; //データ送信用オブジェクト
	private Receiver [] receiver; //データ受信用オブジェクト
	Frame frame;  //画面表示用オブジェクト

	//コンストラクタ
	public ServerSample2(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		out = new PrintWriter [10]; //データ送信用オブジェクトを10クライアント分用意
		receiver = new Receiver [10]; //データ受信用オブジェクトを10クライアント分用意
		online = new boolean[10]; //オンライン状態管理用配列を用意
		
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ
		private int playerNo; //プレイヤを識別するための番号
		private int playerLevel;
		private String playerName;
		private Boolean useHandi;
		private Boolean ready; //クライアントがメッセージを受け入れられる状態かどうか
		private Boolean weakenHandi; //ハンデを緩めるか
		boolean pass;
		int length;
		boolean disconnectFlag;
		boolean battle;

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, int playerNo ){
			try{
				this.playerNo = playerNo; //プレイヤ番号を渡す
				//System.out.println(this.playerNo);  テスト用
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				ready=false;
				pass=false;
				battle=false;
				boolean deisconnectFlag=false;
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {// データを受信し続ける
					String inputLine = br.readLine();//データを一行分読み込む
					if (inputLine != null){ //データを受信したら
						
						if(inputLine.startsWith("PLAYERNAME:")) {  //メッセージテンプレPLAYERNAME:4,kenta,yes(レベル,名前,ハンデ有無)
							length=inputLine.length();
							playerLevel=Integer.valueOf(inputLine.substring(11,12));  //レベルを取得
							if(inputLine.substring(length-3).equals("yes")) {  //ハンデ有り
								useHandi=true;
								playerName=inputLine.substring(13,length-4);  //名前を取得
							}else {  //ハンデ無し
								useHandi=false;
								playerName=inputLine.substring(13,length-3);  //名前を取得
							}
							battle=true;
							if(playerNo%2==1) {  //奇数番目プレイヤー
								while(true) {  //相手が現れるのを待つ
									try {
										Thread.sleep(100);
									}catch(InterruptedException e1) {
									}
									if(receiver[playerNo-2*(playerNo%2)+1].battle==true) {
										break;
									}
								}
								sendColor(playerNo,0);  //先手後手、ハンデの送信
								disconnectFlag=false;
							}
							ready=false;
						}
						
						if(inputLine.startsWith("HCHECK:")) {  //ハンデの諾否テンプレ：HCHECK:STAY(DOWN)
							
							if(inputLine.substring(7).equals("STAY")) {  //承認
								weakenHandi=false;
								ready=true;
							}else {  //DOWN,拒否
								weakenHandi=true;
								ready=true;
							}
							
							while(true) {  //相手の入力待ち
								try {
									Thread.sleep(100);
								}catch(InterruptedException e1) {
								}
								
								if(receiver[playerNo-2*(playerNo%2)+1].ready==true) {
									break;
								}
							}
							
							if(playerLevel<receiver[playerNo-2*(playerNo%2)+1].playerLevel) {  //レベルの低いプレイヤー
								if(weakenHandi==true) {
									sendColor(playerNo-(playerNo%2)+1,1);  //ハンデを緩めて再送
								}else {
									sendColor(playerNo-(playerNo%2)+1,0);  //ハンデを緩めず再送
								}
							}else if(playerLevel==receiver[playerNo-2*(playerNo%2)+1].playerLevel) {
								if(playerNo%2==1) {  //レベルが同じときは奇数プレイヤーに
									sendColor(playerNo,0);
								}
							}
						}
						
						if(inputLine.startsWith("XY:")) {  //座標テンプレ：XY:56
							pass=false;
							receiver[playerNo-2*(playerNo%2)+1].pass=false;
							forwardMessage(inputLine,playerNo-2*(playerNo%2)+1);  //座標を相手に送信
						}
						
						if(inputLine.startsWith("PASS")) {  //パス
							pass=true;  //パスをした
							if(receiver[playerNo-2*(playerNo%2)+1].pass) {  //相手も直前にパスしているとき
								forwardMessage("FINISH:END",playerNo-2*(playerNo%2)+1);  //パスしたことを相手クライアントに送信
								forwardMessage("FINISH:END",playerNo);  //パスしたことを自身の対応クライアントに送信
								disconnectFlag=true;
								receiver[playerNo-2*(playerNo%2)+1].disconnectFlag=true;
							}else {
								forwardMessage(inputLine,playerNo-2*(playerNo%2)+1);  //パスしたことを相手に送信
							}
						}
						
						if(inputLine.startsWith("OFUZAKE")) {  //遊び要素
							forwardMessage(inputLine,playerNo-2*(playerNo%2)+1);
						}
						
						if(inputLine.equals("SURRENDER")) {
							forwardMessage(inputLine,playerNo);
							forwardMessage(inputLine,playerNo-2*(playerNo%2)+1);
							battle=false;
							disconnectFlag=true;
							receiver[playerNo-2*(playerNo%2)+1].disconnectFlag=true;
						}
						
						if(inputLine.equals("SHUFFLE")) {
							battle=false;
							disconnectFlag=true;
							receiver[playerNo-2*(playerNo%2)+1].disconnectFlag=true;
						}
						inputLine=null;
					}
				}
			} catch (IOException e){ // 接続が切れたとき
				System.err.println("プレイヤ " + playerNo + "との接続が切れました．");
				receiver[playerNo].playerLevel=0;  //receiverを初期化
				receiver[playerNo].playerName=null;
				receiver[playerNo].useHandi=false;
				receiver[playerNo].weakenHandi=false;
				receiver[playerNo].pass=false;
				receiver[playerNo].battle=false;
				out[playerNo]=null;
				
				online[playerNo] = false; //プレイヤの接続状態を更新する
				if((online[playerNo-2*(playerNo%2)+1]==true)&&(disconnectFlag==false)) {
					forwardMessage("FINISH:DISCONNECT",playerNo-2*(playerNo%2)+1);  //対戦相手に対局終了を送信
				}
				printStatus(); //接続状態を出力する
			}
		}
	}

	// メソッド

	public void acceptClient(){ //クライアントの接続(サーバの起動)
		int i=0;
		boolean flag=false;
		//String message;
		frame=new Frame("プレイヤー接続状況");
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意
			while (true) {
				printStatus();
				Socket socket = ss.accept(); //新規接続を受け付ける
				System.out.println("クライアントと接続しました．"); //テスト用出力
				
				for(i=0;i<9;i++) {
					if((online[i]==true)&&(online[i-2*(i%2)+1]==false)) {
						i=i-2*(i%2)+1;
						flag=true;
						break;
					}
				}
				if((i%2==0)&&(flag==false)) {  //空き番号にプレイヤーを入れる
					for(i=0;i<9;i=i+2) {
						if(online[i]==false&&online[i+1]==false) {
							break;
						}
					}
				}else if((i%2==1)&&(flag==false)) {
					if(online[i-1]==false) {
						for(i=0;i<9;i=i+2) {
							if(online[i]==false&&online[i+1]==false) {
								break;
							}
						}
					}
				}
			    receiver[i]=new Receiver(socket,i);
			    out[i]=new PrintWriter(socket.getOutputStream(), true);
			    online[i]=true;
			    printStatus();
				receiver[i].start();
				System.out.println("プレイヤー番号"+i);
				i=0;
				flag=false;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	public void printStatus(){ //クライアント接続状態の確認
		frame.myCanvas.repaint();
	}

	public void sendColor(int playerNo,int weakenHandi){ //先手後手情報(白黒)とハンデの送信、引数は奇数、ハンデ調整
		int handicap;
		if(receiver[playerNo-1].playerLevel<receiver[playerNo].playerLevel) {  //奇数番目が強いとき
			handicap=receiver[playerNo].playerLevel-receiver[playerNo-1].playerLevel-weakenHandi;
			if(receiver[playerNo-1].useHandi==false) {
				handicap=0;
			}
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"WHITE",playerNo);  //メッセージテンプレ：HANDI:3,BLACK
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"BLACK",playerNo-1);
			
		}else if(receiver[playerNo-1].playerLevel>receiver[playerNo].playerLevel) {  //偶数番目が強いとき
			handicap=receiver[playerNo-1].playerLevel-receiver[playerNo].playerLevel-weakenHandi;
			if(receiver[playerNo].useHandi==false) {
				handicap=0;
			}
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"WHITE",playerNo-1);
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"BLACK",playerNo);
		}else {  //レベルが同じ
			handicap=0;
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"BLACK",playerNo-1);  //playerNoが若い方が先手
			forwardMessage("HANDI:"+String.valueOf(handicap)+","+"WHITE",playerNo);
		}
	}

	public void forwardMessage(String msg, int playerNo){ //操作情報の転送
		
		try {
			out[playerNo].println(msg);//送信データをバッファに書き出す
			out[playerNo].flush();//送信データを送る
			System.out.println("クライアント"+(playerNo)+"にメッセージ " + msg + " を送信しました"); //テスト標準出力
				
		}catch(Exception e) {
			System.err.println("プレイヤ " + playerNo + "との接続が切れました．");
			online[playerNo] = false; //プレイヤの接続状態を更新する
			printStatus(); //接続状態を出力する
		}
		
	}
	
	class NewCanvas extends Canvas {
		NewCanvas(){
		}
		
		public void paint(Graphics g) {
			int height=this.getHeight();
			int width=this.getWidth();
			
			for(int j=0;j<10;j++) {  //各プレイヤーに対し、接続：緑、非接続：灰
				if(online[j]) {
					g.setColor(Color.green);
				}else {
					g.setColor(Color.gray);
				}
				g.fillArc(2*width/20*j+width/30,height/2,width/20,width/20,0,360);  //等間隔に10個接続情報を表示
				
				Font font=new Font("ゴシック",Font.PLAIN,width/30);
				g.setFont(font);
				g.setColor(Color.black);
				g.drawString(String.valueOf(j), 2*width/20*j+width/20, height*2/3);
			}
			
			g.setColor(Color.blue);
			g.fillArc(width, height, width/20, height/20, 0, 360);
		}
	}
	
	class Frame extends JFrame implements ActionListener {  //クライアント接続情報
		NewCanvas myCanvas;
		JPanel panel;
		JLabel label;
		JButton b0;
		Random rand=new Random();
		Frame(String title){
			super(title);
			System.out.println("a");
			setLayout(new GridLayout(2,1));
			myCanvas=new NewCanvas();
			setSize(400,1200);
			add(myCanvas);
			b0=new JButton("⚠");
			b0.setFont(new Font("ゴシック",Font.BOLD,50));
			b0.setForeground(Color.RED);
			b0.setBackground(Color.YELLOW);
			b0.addActionListener(this);
			b0.setPreferredSize(new Dimension(200,100));
			panel=new JPanel();
			panel.add(b0);
			add(panel);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(800,1200);
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e) {  //イベントを起こすボタン
			
			if(e.getActionCommand().equals("⚠")) {
				for(int i=0;i<10;i++) {
					forwardMessage("FINISH:DISCONNECT",i);
				}
				
			}
		}
	}

	public static void main(String[] args){ //main
		ServerSample2 server = new ServerSample2(10000); //待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptClient(); //クライアント受け入れを開始
	}
}