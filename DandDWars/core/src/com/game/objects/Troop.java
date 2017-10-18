package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

public class Troop{
	int health;
	int speed;
	int damage;
	int defense;
	int team;
	int attackRangeMin;
	int attackRangeMax;

	//This is used for animations
	public float stateTime;
	
	Vector2 position;
	boolean faceRight;
	Animation animation;
	Texture texture;
	
	public Animation<TextureRegion> a;
	
	//Trying to use enum for expandibility of more types
	public enum TROOP_TYPE {
		KNIGHT,
		ARCHER,
		WIZARD
		//Megatank xD
	}
	
	public TROOP_TYPE troopType;
	
	
	
	public Troop (String type, int t, int posx, int posy) {
		setType(type);
		init(t, posx, posy);
	}
	
	
	//This also needs to be touched up, I just got it to compile
	public void init(int t, int posx, int posy){
					  
		switch (troopType) {
			case KNIGHT: 
				//INFANTRY or SWORDSMEN
				createKnight();
				break;
			case ARCHER: 
				//BOWMEN or MECH
				createArcher();
				break;
			case WIZARD: 
				//MAGES or TANKS
				createWizard();
				break;
			default: 
				//???
				break;
		}

		position = new Vector2();
		updatePos(posx,posy);

		team = t;
		//initialize other values
		////team = t;
		////faceRight = faceR;
		////animation = a;
		////position.x = x;
		////position.y = y;
		////attackRange = {attackRMIN, attackRYMAX}
		//however tuples are done^
	}
	
	//Takes a string and turns it into an enum
	/* 
	 * Java is a little silly and doesn allow strings
	 * within a switch statement, so here we are
	 */
	public void setType(String type){
		int typeNum = 0;
		
		     if(type == "Knight" || type == "knight") typeNum = 1;
		else if(type == "Archer" || type == "archer") typeNum = 2;
		else if(type == "Wizard" || type == "wizard") typeNum = 3;
				
		switch(typeNum){
			case 1:
				troopType = TROOP_TYPE.KNIGHT;
			break;
			case 2:
				troopType = TROOP_TYPE.ARCHER;
			break;
			case 3:
				troopType = TROOP_TYPE.WIZARD;
			break;
			default:
			//Print out error message?

		}
	}
	
	public void setAnimation(TROOP_TYPE type){
		
	}
	
	/*updatePos
	 * 
	 *add or subtract from x and y the amount
	 *of spaces*the pixel width of one space
	 *with relation to the amount of speed the 
	 *troop actually has and where they went.
	 */
	public void updatePos(int posx, int posy) {
		position.x = posx * 16;
		position.y = posy * 16;
	}
	
	// Returns the position, could be handy
	public Vector2 getPos(){
		return position;
	}
	
	/*updateHealth
	 * 
	 * Remove the amount of damage that
	 * is passed to the function 
	 */
	 
	public void updateHealth(int incomingDamage){
		//d is damage being delt to the troop
		health -= incomingDamage;
	}
	
	/*giveDamage
	 * 
	 * Calculate the amount of damage
	 * that is given to the enemy in 
	 * relation to the defense of the enemy
	 * Damage is (so far):
	 * 		damage of troop - defense of enemy troop
	 */
	
	public int giveDamage(int defEnemy){
		int d;
		d = damage-defEnemy;
		if (d > 0){
			return d;
		} else {
			return 0;
		}
	}
	
	public void render (SpriteBatch batch){

		// below statements will be used once we have animation
		/*
		 *TextureRegion reg = null;
		 *reg = animation.getKeyFrame(stateTime,true);
		 *batch.draw(reg.getTexture(), position.x, position.y);
		 */
		texture = new Texture("unit_tiles/KnightRed.png");
		batch.draw(texture, position.x, position.y);
		
	}

	
	
	/*-------------------------------------------------------------------*/
	/*----------------------Troop Creation Functions---------------------*/
	/*-------------------------------------------------------------------*/

	public void createKnight(){
		health = 10;
		speed = 3;
		damage = 5;
		defense = 3;
		attackRangeMin = 1;
		attackRangeMax = 1;
	}
	
	public void createArcher(){
		health = 5;
		speed = 5;
		damage = 7;
		defense = 1;
		attackRangeMin = 2;
		attackRangeMax = 6;
	}
	
	public void createWizard(){
		health = 10;
		speed = 2;
		damage = 10;
		defense = 7;
		attackRangeMin = 3;
		attackRangeMax = 9;
	}
	
	
	
	
}
