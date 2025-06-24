public class Othello {
	private int row = 8;	//オセロ盤の縦横マス数(2の倍数のみ)
	private String [] grids = new String [row * row]; //局面情報
	private String [][] board = new String[row][row];
	//private 

	// コンストラクタ
	public Othello(){
		
	}

	//ゲームの終了判定 盤面が埋まるか、どちらかの石が0になった場合にゲームを終了する
	public int isGameover(String grids[]) {
		int b=0;int w=0;int s=0;
		for(int i=0;i<64;i++) {
			switch(grids[i]) {
			case "black":
				b++;
				break;
			case "white":
				w++;
				break;
			case "board":
				s++;
				break;
			}
		}
		if(b==0||w==0||s==0)return 1;
		else return 0;
	}
	
	//勝敗判定
	public String checkWinner(String grids[],int handy){
		int b=0;int w=0;int s=0;
		for(int i=0;i<64;i++) {
			switch(grids[i]) {
			case "black":
				b++;
				break;
			case "white":
				w++;
				break;
			default:
				break;
			}
		}
		String winner="-1";//なんかのバグで勝敗判定できなかった場合は"-1"が返される
		if(b>w) {
			winner="black";
		}else if(b==w) {
			//ハンディキャップがある場合、同点は黒の勝ちになる そうでなければ引き分け
			if(handy==1) {
				winner="black";
			}else {
				winner="draw";
			}
		}else {
			winner="white";
		}
		return winner;
		
	}
		
	//石を置く
	public String[] putStone(String grids[], int pos, int colour) {
		
		int flag=0;//石がおけたかどうか 置けた場合は1、置けなかった場合は0となる
		
		if(grids[pos].equals("board")) {
			for(int i=0;i<8;i++) { //一次元の盤面情報を二次元に変換
				for(int j=0;j<8;j++) {
					/* 盤面が正しく反映されているかの確認用
					switch(grids[i*8+j]) {
					case "black":
						System.out.print("◯");
						break;
					case "white":
						System.out.print("●");
						break;
					case "board":
						System.out.print("死");
						break;
					}
					*/
					board[i][j]=grids[i*8+j];
					//System.out.print(board[i][j]);
				}
				//System.out.println();
			}
			
			int init_x=pos%8; //石が置かれたx座標
			int init_y=pos/8; //石が置かれたy座標
			
			//色情報をint→Stringに変換
			String color=new String();
			if(colour==0) {
				color="black";
			}else {
				color="white";
			}
			
			String stone=color;
			
			//石を置く処理 現在のマスから8方向を順番に調べていく
			for(int i=-1;i<2;i++) {
				if(init_y+i>=0||init_y+i<8) { //調べる範囲が盤面からはみ出さないようにする
					for(int j=-1;j<2;j++) {
						
						int x=init_x;
						int y=init_y;
						int c=0;//キュー用のカウンタ
						int[][] queue = new int[8][2];//反転させる可能性のある石をキューに入れていく
						queue[0][0]=-1;//キューの先頭を-1にすることで、隣が同色の場合反転させないための検出をできるようにする
						
						int edge=1;//調べていって端が自分の色と同じ石の場合、0にする
						if(init_x+j>=0||init_x+j<8) {	//調べる範囲が盤面からはみ出さないようにする
							while(c<8) {
							//whileの条件: 何も石が置かれていない、または盤面の範囲外
								x+=j; y+=i;
								if(x<0||x>7||y<0||y>7) break;//範囲外の場合、break
								stone=board[y][x];
								if(stone.equals("board")) {
									break;//石が置かれていない場合、break
								}else if(!stone.equals(color)) {
									//石が自分の色と違う場合、キューに場所を保管する
									queue[c][0]=x;
									queue[c][1]=y;
									c++;
								}else {
									edge=0;
									break;
								}
							}
							if(edge==0&&queue[0][0]!=-1) {//キューの先頭が-1でない(隣り合った同色じゃない)かつ調べていった端が同色の場合、キューの先頭から石を反転させていく
								//System.out.println("石おくぜ！");
								flag=1;//石がおけるのでflagを1にする
								for(int k=c-1;k>=0;k--) {
									if(board[queue[k][1]][queue[k][0]]!="board") {
										board[queue[k][1]][queue[k][0]]=color;	
									}
								}
								board[init_y][init_x]=color;//現在位置の更新も忘れないようにね♡
							}
						}
					}
				}
			}
		}
		
		
		//盤面確認のための出力
		/*
		System.out.println("---------------------------------");
		for(int i=0;i<8;i++) { 
			for(int j=0;j<8;j++) {
				switch(board[i][j]) {
				case "black":
					System.out.print("●");
					break;
				case "white":
					System.out.print("◯");
					break;
				case "board":
					System.out.print("死");
				}
			}
			System.out.println();
		}
		System.out.println("---------------------------------");
		*/
		
		//二次元情報を一次元に変換
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				grids[i*8+j]=board[i][j];
			}
		}
		
		//石がおけなかった場合
		if(flag==0) {
			grids[0]="-1";
		}
		
		return grids;
		
	}
	
	//詰みかどうか判定する
	//すべてのマスでその石がおけるかどうか判定し、どこにも置けない場合詰みと判定する
	public int checkMate(String grids1[], int colour){
	    int flag = 0;
	    for(int i = 0; i < 64; i++) {
	        // 安全のため毎回 clone して渡す
	        String[] testGrid = grids1.clone();  
	        String[] result = putStone(testGrid, i, colour);

	        // 有効な手なら "-1" ではない
	        if (!result[0].equals("-1")) {
	            flag = 1;
	            break; // 1つでも置けたら true（早期returnもOK）
	        }
	    }
	    return flag;
	}

	
}