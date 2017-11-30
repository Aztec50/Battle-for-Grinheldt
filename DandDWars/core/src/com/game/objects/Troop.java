package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Troop{
	public int health;
	int maxHealth;
	public int speed;
	public int damage;
	public int defense;
	public int attackRangeMin;
	public int attackRangeMax;
	public boolean moved;
	public boolean attacked;
	public boolean dead;
	
	public Rectangle bounds;

	//This is used for animations
	public float stateTime;
	
	Vector2 position;
	boolean faceRight;
	Texture texture;

	TextureAtlas animationAtlas;
	public Animation<TextureRegion> animation;
	
	//Trying to use enum for expandibility of more types
	public enum TROOP_TYPE {
		KNIGHT,
		ARCHER,
		WIZARD
		//Megatank xD 
		//:P https://images-na.ssl-images-amazon.com/images/M/MV5BYjdlYjM2NGItZTY0Mi00NmVjLWIwMTAtNTBiZTg4NTc3NGJjXkEyXkFqcGdeQXVyNjExODE1MDc@._V1_UY268_CR76,0,182,268_AL_.jpg
	}
	public enum ACTION {
		MOVE,
		ATTACK,
		IDLE
	}
	public enum TEAM{
		RED,
		BLUE
	}
	
	public TROOP_TYPE troopType;
	public TEAM team;
	public ACTION state;
	
	
	public Troop (String type, String t, int posx, int posy, boolean[][] troopOn, boolean[][] troopTeam) {
		setType(type);
		setTeam(t);
		init(posx, posy);
		troopOn[((int)position.x/32)][((int)position.y/32)] = true;
		
		//in terms of troopTeam, RED = false   BLUE = true
		switch(team) {
			case RED: 
				troopTeam[((int)position.x/32)][((int)position.y/32)] = false;
				break;
			case BLUE:
				troopTeam[((int)position.x/32)][((int)position.y/32)] = true;
				break;
		}
	}
	
	
	//This also needs to be touched up, I just got it to compile
	public void init(int posx, int posy){
		
		//Initializes bounds, gets set with position later
		bounds = new Rectangle(0,0, 32, 32);
		
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
		//updatePos(posx,posy, land);
		position.x = posx*32;
		position.y = posy*32;
		bounds.x = posx * 32;
		bounds.y = posy * 32;	
		moved = false;	
		attacked = false;
		dead = false;
		maxHealth = health;
		state = ACTION.IDLE;
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
	public void setTeam(String t){
		int teamNum = 0;
		
			 if(t == "red" || t == "Red") teamNum = 1;
		else if(t == "blue" || t == "Blue") teamNum = 2;
		
		switch(teamNum){
			case 1:
				team = TEAM.RED;
			break;
			case 2:
				team = TEAM.BLUE;
			break;
			default:
			//potential error message?
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
	public void updatePos(int posx, int posy, boolean[][] troopOn, boolean[][] troopTeam, boolean[][] drawTiles) {
		if (!troopOn[posx][posy] && drawTiles[posx][posy]) {
			troopOn[((int)position.x/32)][((int)position.y/32)] = false;
			position.x = posx * 32;
			position.y = posy * 32;
			bounds.x = posx * 32;
			bounds.y = posy * 32;
			troopOn[((int)position.x/32)][((int)position.y/32)] = true;
			
			//in terms of troopTeam, RED = false   BLUE = true
			switch(team) {
				case RED: 
					troopTeam[((int)position.x/32)][((int)position.y/32)] = false;
					break;
				case BLUE:
					troopTeam[((int)position.x/32)][((int)position.y/32)] = true;
					break;
			}
		}
	}
	/*
	 * old move function
	public void updatePos(int posx, int posy, boolean[][] troopOn, boolean[][] troopTeam, boolean[][] drawTiles) {
		if (!troopOn[((int)position.x/32)+posx][((int)position.y/16)+posy] && drawTiles[((int)position.x/16)+posx][((int)position.y/16)+posy]) {
			troopOn[((int)position.x/16)][((int)position.y/16)] = false;
			position.x += posx * 16;
			position.y += posy * 16;
			bounds.x += posx * 16;
			bounds.y += posy * 16;
			troopOn[((int)position.x/16)][((int)position.y/16)] = true;
			
			//in terms of troopTeam, RED = false   BLUE = true
			switch(team) {
				case RED: 
					troopTeam[((int)position.x/16)][((int)position.y/16)] = false;
					break;
				case BLUE:
					troopTeam[((int)position.x/16)][((int)position.y/16)] = true;
					break;
			}
		}
	}
	*/
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
		if (health <= 0) {
			dead = true;
		}
	}
	
	/*giveDamage
	 * 
	 * Calculate the amount of damage
	 * that is given to the enemy in 
	 * relation to the defense of the enemy
	 * Damage is (so far):
	 * 		damage of troop - defense of enemy troop
	 * Possible suggestion for improved damage:
	 * luck = 1.0 to 1.1
	 * Damage = Damage * luck - enemy def - terrain 
	 *
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
	
	public void setAnimation (Animation animation) {
		this.animation = animation;
		stateTime = 0;
	}
	
	public void update (float deltaTime) {
		stateTime += deltaTime;
	}
	
	public void render (SpriteBatch batch, ShapeRenderer sr, int panOffsetX, int panOffsetY){
		int healthBarGreen, healthBarRed;

		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime,true);

		/*  This draw function was a doozy to figure out, but as it is
         *  it should draw and animate all of our units. In the case that
         *  something needs to be changed, the prototype is provided. 
		 *
         *  draw(Texture texture, float x, float y, float width, float height,
		 *       int srcX, int srcY,
		 *		 int srcWidth, int srcHeight,
		 *		 boolean flipX, boolean flipY);
		 */
		
		batch.draw(reg.getTexture(), position.x, position.y, 32, 32,
				   reg.getRegionX(), reg.getRegionY(),
				   reg.getRegionWidth(), reg.getRegionHeight(),
				   false, false);
		batch.end();
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.GREEN);
		healthBarGreen = (int)((28f*(float)((float)health/(float)maxHealth)));
		healthBarRed = 28 - healthBarGreen;
		sr.rect(position.x+2-panOffsetX, position.y+2-panOffsetY, healthBarGreen,4);
		sr.setColor(Color.RED);
		sr.rect(position.x+2+healthBarGreen-panOffsetX, position.y+2-panOffsetY, healthBarRed, 4);
		sr.end();
		batch.begin();
	}

	
	
	/*-------------------------------------------------------------------*/
	/*----------------------Troop Creation Functions---------------------*/
	/*-------------------------------------------------------------------*/

	public void createKnight(){
		
		String fileSourceAtlas = "";
		String fileSourceAnimation = "";
		
		// This needs to be better generalized
		if(team == TEAM.RED){
			fileSourceAtlas = String.format("unit_animations/RedKnightAnimation.atlas");
			fileSourceAnimation = String.format("RedKnightIdle");
		}else if(team == TEAM.BLUE){
			fileSourceAtlas = String.format("unit_animations/BlueKnightAnimation.atlas");
			fileSourceAnimation = String.format("BlueKnightIdle");
		}
		animationAtlas = new TextureAtlas(Gdx.files.internal(fileSourceAtlas));
		animation = new Animation<TextureRegion>(0.3f, animationAtlas.findRegions(fileSourceAnimation), PlayMode.LOOP);

		
		stateTime = 0;
		
		
		health = 10;
		speed = 3;
		damage = 5;
		defense = 3;
		attackRangeMin = 1;
		attackRangeMax = 2;
	}
	
	public void createArcher(){
		
		String fileSourceAtlas = "";
		String fileSourceAnimation = "";
		
		// This needs to be better generalized
		if(team == TEAM.RED){
			fileSourceAtlas = String.format("unit_animations/RedArcherAnimation.atlas");
			fileSourceAnimation = String.format("RedArcherIdle");
		}else if(team == TEAM.BLUE){
			fileSourceAtlas = String.format("unit_animations/BlueArcherAnimation.atlas");
			fileSourceAnimation = String.format("BlueArcherIdle");
		}
		animationAtlas = new TextureAtlas(Gdx.files.internal(fileSourceAtlas));
		animation = new Animation<TextureRegion>(0.3f, animationAtlas.findRegions(fileSourceAnimation), PlayMode.LOOP);

		
		stateTime = 0;
		
		
		
		health = 5;
		speed = 5;
		damage = 7;
		defense = 1;
		attackRangeMin = 2;
		attackRangeMax = 6;
	}
	
	public void createWizard(){
		
		String fileSourceAtlas = "";
		String fileSourceAnimation = "";
		
		// This needs to be better generalized
		if(team == TEAM.RED){
			fileSourceAtlas = String.format("unit_animations/RedWizardAnimation.atlas");
			fileSourceAnimation = String.format("RedWizardIdle");
		}else if(team == TEAM.BLUE){
			fileSourceAtlas = String.format("unit_animations/BlueWizardAnimation.atlas");
			fileSourceAnimation = String.format("BlueWizardIdle");
		}
		animationAtlas = new TextureAtlas(Gdx.files.internal(fileSourceAtlas));
		animation = new Animation<TextureRegion>(0.3f, animationAtlas.findRegions(fileSourceAnimation), PlayMode.LOOP);

		
		stateTime = 0;
		
		
		
		health = 4;
		speed = 2;
		damage = 8;
		defense = 6;
		attackRangeMin = 3;
		attackRangeMax = 9;
	}
	
	
	
	
}
