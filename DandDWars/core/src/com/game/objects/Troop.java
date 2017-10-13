package com.game.objects;

public class Troop{
	int health;
	int troopType;
	int speed;
	int damage;
	int defense;
	int team;
	tuple attackRange;//dont know much about tuples
	Vector2 position;
	boolean faceRight;
	Animation animation;
	
	public Troop (int type, int team, 
				  int x, int y, 
				  attackRMIN, int attackRMAX
				  Animation a,
				  boolean faceR) {
		troopType = type			  
		switch (troopType) {
			case 1: {
				//INFANTRY or SWORDSMEN
				health = 10;
				speed = 3;
				damage = 5;
				defense = 3;
				break;
			}
			case 2: {
				//BOWMEN or MECH
				health = 5;
				speed = 5;
				damage = 7;
				defense = 1;
				break;
			}
			case 3: {
				//MAGES or TANKS
				health = 10;
				speed = 2;
				damage = 10;
				defense = 7;
				break;
			}
			case 4: {
				//???
				break;
			}
		}
		//initialize other values
		team = t;
		faceRight = faceR;
		animation = a;
		position.x = x;
		position.y = y;
		attackRange = {attackRMIN, attackRYMAX}
		//however tuples are done^
	}
	
	/*updatePos
	 * 
	 *add or subtract from x and y the amount
	 *of spaces*the pixel width of one space
	 *with relation to the amount of speed the 
	 *troop actually has and where they went.
	 */
	public void updatePos() {
	
	}
	/*updateHealth
	 * 
	 * Remove the amount of damage that
	 * is passed to the function 
	 */
	public void updateHealth(int d){
		//d is damage being delt to the troop
		health -=d;
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
}
