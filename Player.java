public class Player {
	String name;
	int level;
	int BrackorWhite;

	public void setName(String myName) {//プレイヤ名の受付
		name = myName;
	}
	
	public String getName() {//プレイヤ名の取得
		return name;
	}
	
	public void setLevel(int myLevel) {
		level = myLevel;
	}
	
	public int getLevel() {//プレイヤレベルの取得
		return level;
	}
	
	public void setColor(int c){ // 先手後手情報の受付
		BrackorWhite = c;
	}
	
	public int getColor(){ // 先手後手情報の取得
		return BrackorWhite;
	}
}