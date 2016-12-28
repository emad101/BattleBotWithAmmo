/**
* 
*/
package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The TheBEST is a Bot that is awesome. It will dodge all attacks and shoot at
 * enemies. It also turns if it detects that it has hit something.
 * 
 * @author 1shaikhdan Critical Requirements: Bot will dodge incoming bullets Bot
 *         will fire at other Bot Programs (Bot will sort through array to find
 *         the most dangerous bullets.) (Bot will put weightings to possible
 *         movement commands based on the bullets.) Bot will have a unique image
 *         & namehttps://help.ubuntu.com/community/isc-dhcp-server
 * 
 *         Optional Requirements: Bot sends messages to other bots Bot uses
 *         simple learning Bot adapts and gets better over time
 *
 */
public class TheBEST extends Bot {

	/**
	 * My name
	 */
	String name;
	/**
	 * My next message or null if nothing to say
	 */
	String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = { "Wombo Combo!!!", "Ezzz!", "Ha Ha! Got 'emm!", "What are those?", "Megusta! :-)",
			"Ya Boi" };
	/**
	 * Image for drawing
	 */
	Image standard, blink, current;
	/**
	 * For deciding when bot is safe and not stuck.
	 */
	private int counter = 3;
	/**
	 * Integer to split movement.
	 */
	private int lastmovement = 1;
	/**
	 * For determining when bot is stuck on wall (keeps moving into a wall).
	 */
	private boolean stuckOnWall = false;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
	/**
	 * The location (coordinates) of the bot two runs ago. Values are compared
	 * to bots current coordinates. If values are the same it means that bot has
	 * not moved.
	 */
	private double xtheBest = 0, ytheBest = 0;
	/**
	 * Used to determine the final direction of the robot.
	 */
	private int movement = 0;

	/**
	 * Draw the current Drone image
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
	}

	/**
	 * The Magic.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

		// occasional messages
		if (Math.random() < 0.02) {
			nextMessage = killMessages[(int) (Math.random() * killMessages.length)];
			return BattleBotArena.SEND_MESSAGE;
		}

		// When the x and y values of the robot have not changed, and the bot is
		// not trying to stay still,
		// the bot must be stuck on a wall.
		if (me.getX() == xtheBest && me.getY() == ytheBest && move != BattleBotArena.STAY && stuckOnWall == false) {
			stuckOnWall = true;
		}

		// Updates the stored x and y coordinates of the robot every two runs.
		if (lastmovement == 1) {
			xtheBest = me.getX();
			ytheBest = me.getY();
		} else if (lastmovement == 2) {
			lastmovement = 0;
		}
		lastmovement++;

		// When the bot is not stuck. Sets coutner to 3.
		if (stuckOnWall == false) {
			counter = 4;
		}

		// If the bot is stuck and the counter has fully counted down, the bot
		// should no longer be stuck.
		if (stuckOnWall == true && counter == 0) {
			stuckOnWall = false;
		}

		// When the robot is not stuck
		// if (stuckOnWall == false){
		// movement = dodgeMovement(me, bullets);
		// }

		// When the bot is not stuck and is not safe.
		// if (stuckOnWall == false){

		// Sets image to default image.
		current = standard;
			
		/*
		 * This function makes the bots reduce their bullets in the screen once
		 * they have low ammo left. if the bot have less than 10 bullets he will
		 * only be able to shoot one bullet at a time. if he have 20 he will be
		 * able to shoot 2 at a time. if he have 30 he will be able to shoot 3 at
		 * a time. if he have more than 30 he will be able to shoot 4 at a time
		
		BattleBotArena ok = new BattleBotArena();
		int BulletsInScreen = ok.NUM_BULLETS;
		if (me.getBulletsLeft() <= 10) {
			BulletsInScreen = 1;
			System.out.println("It works 1");
		} else if (me.getBulletsLeft() > 10 && me.getBulletsLeft() <= 20) {
			BulletsInScreen = 2;
			System.out.println("It works 2");
		} else if (me.getBulletsLeft() > 20 && me.getBulletsLeft() <= 30) {
			BulletsInScreen = 3;
			System.out.println("It works 3");
		} else {
			BulletsInScreen = 4;
		}
		 */

		/*
		 * The order in which final movements are decided. Priorities: (When bot
		 * has more than 4 bullets) 1. Dodge Bullets 2. Shooting at Enemies 3.
		 * Avoiding Edges/Boundaries 4. Avoiding Enemies 5. Scavenging for
		 * bullets 6. Avoiding/Engaging others a. Engage when there are less
		 * that 6 other bots b. Avoid when there are more
		 * 
		 */
		if (me.getBulletsLeft() >= 5) {
			movement = dodgeMovement(me, bullets);
			if (movement == 0) { // If there are no bullets to dodge...
				movement = fireDircetion(me, liveBots);
				if (movement == 0) { // If there are no enemies to shoot at...
					movement = avoidBoundries(me);
					if (movement == 0) { // If bot is not near any boundaries...
						movement = deadPoints(me, deadBots);
						System.out.println("deadPoints - A Testing");
						if (movement == 0) { // If bot touches the deadbot move
												// away from it...^
							movement = moveAway(me, deadBots);
						}
						if (movement == 0) { // If there are not any bullets no
												// scavenge...
							if (liveBots.length < 6) { // When there are less
														// than 6 bots
														// remaining...
								movement = engageEnemyRobots(me, liveBots);
								System.out.println("engageEnemyRobots - A Testing");
							} else { // When there are more than 6 bots
										// remaining...
								movement = avoidEnemyRobots(me, liveBots);
								System.out.println("avoidEnemyRobots - A Testing");
							}
						}
					}
				}
			}
		}

		/*
		 * The order in which final movements are decided. Priorities: (When bot
		 * has less than 4 bullets) 1. Dodge Bullets 2. Scavenging for bullets
		 * 3. Avoiding Edges/Boundaries 4. Avoiding Enemies 5. Shooting at
		 * Enemies 6. Avoiding/Engaging others a. Engage when there are less
		 * that 6 other bots b. Avoid when there are more
		 */
		if (me.getBulletsLeft() < 5) {
			movement = dodgeMovement(me, bullets);
			if (movement == 0) { // If there are no bullets to dodge...
				movement = deadPoints(me, deadBots);
				System.out.println("deadPoints - B Testing");
				if (movement == 0) { // If bot touches the deadbot move away
										// from it...^
					movement = moveAway(me, deadBots);

				}
				if (movement == 0) { // If there are no bullets to scavange...
					movement = avoidBoundries(me);
					System.out.println("avoidBoundries - B Testing");
					if (movement == 0) {
						movement = fireDircetion(me, liveBots);
						System.out.println("fireDricetion - B Testing");
						if (movement == 0) { // If our bot is not near any ////
												// boundaries...\
							if (liveBots.length < 5) {
								movement = engageEnemyRobots(me, liveBots);
								System.out.println("engageEnemyRobots - B Testing");
							} else {
								movement = avoidEnemyRobots(me, liveBots);
								System.out.println("avoidEnemyRobots - B Testing");
							}
						}
					}
				}
			}
		}

		switch (movement) {
		case 0:
			move = BattleBotArena.STAY;
			break;
		case 1:
			move = BattleBotArena.UP;
			break;
		case 2:
			move = BattleBotArena.DOWN;
			break;
		case 3:
			move = BattleBotArena.LEFT;
			break;
		case 4:
			move = BattleBotArena.RIGHT;
			break;
		case 5:
			move = BattleBotArena.FIREUP;
			current = blink;
			break;
		case 6:
			move = BattleBotArena.FIREDOWN;
			current = blink;
			break;
		case 7:
			move = BattleBotArena.FIRELEFT;
			current = blink;
			break;
		case 8:
			move = BattleBotArena.FIRERIGHT;
			current = blink;
			break;
		}
		return move;
	}
	/*
	 * if (stuckOnWall == true) { counter--; //Moves in this direction for
	 * "counter" runs. switch (movement){ //Move out of the way of incoming
	 * bullets. IN THE OPPOSITE DIRECTION OF THE INTENDED DIRECTION. case 1:
	 * move = BattleBotArena.DOWN; return move; case 2: move =
	 * BattleBotArena.UP; return move; case 3: move = BattleBotArena.RIGHT;
	 * return move; case 4: move = BattleBotArena.LEFT; return move; default: }
	 * } return move;
	 * 
	 * }
	 */

	/**
	 * Method for dodging bullets. Determines if the bullet is close to the
	 * robot (via Manhattan distance). Determines the appropriate movement in
	 * order to dodge the bullet based on its position to the bot. If another
	 * bullet it is not closer than a previous bullet, it will not be accounted
	 * for (dangerousbullet). Returns movement.
	 */
	public int dodgeMovement(BotInfo me, Bullet[] bullets) {
		int movement = 0;
		double dangerousbullet = 151;
		for (int i = 0; i < bullets.length; i++) {
			double ybullet = (me.getY() + Bot.RADIUS - bullets[i].getY());
			double xbullet = (me.getX() + Bot.RADIUS - bullets[i].getX());
			double d = Math.abs(xbullet) + Math.abs(ybullet);
			if (d < 150) { // warn if within 150 pixels

				if (d < dangerousbullet) { // When this bullet is closer to the
											// Robot than previously checked
											// bullets.
					dangerousbullet = d;

					if (40 > Math.abs(ybullet) && Math.abs(xbullet) > 11) {
						if (0 < ybullet && ybullet <= 20) {
							movement = 2; // Bullet above Robot.
						} else if (0 >= ybullet && ybullet >= -20) {
							movement = 1; // Bullet below Robot.
						}
					}
					if (Math.abs(xbullet) < 40 && Math.abs(ybullet) > 11) {
						if (0 < xbullet && xbullet <= 20) {
							movement = 4; // Bullet left of Robot.
						} else if (0 >= xbullet && xbullet >= -20) {
							movement = 3; // Bullet right of Robot.
						}
					}
				}
			}
		}
		return movement;
	}

	/**
	 * Method to determine in which direction to shoot. When other robots are
	 * close to near the same x or y value as bot, fires in the direction of the
	 * bot.
	 */
	public int fireDircetion(BotInfo me, BotInfo[] liveBots) {
		double dangerousRobot = 601;
		int shotdirection = 0;
		for (int i = 0; i < liveBots.length; i++) {
			double xshot = (me.getX() + Bot.RADIUS - liveBots[i].getX());
			double yshot = (me.getY() + Bot.RADIUS - liveBots[i].getY());
			double d = Math.abs(me.getX() - liveBots[i].getX()) + Math.abs(me.getY() - liveBots[i].getY());
			if (d < 200) { // warn if within 200 pixels
				if (dangerousRobot > d) {
					dangerousRobot = d;

					if (Math.abs(yshot) <= 10 && Math.abs(xshot) > 11) {
						if (me.getX() <= liveBots[i].getX()) {
							shotdirection = 8; // Enemy Robot to the right.

						} else if (me.getX() >= liveBots[i].getX()) {
							shotdirection = 7; // Enemy Robot to the left.
						}
					} else if (Math.abs(xshot) <= 10 && Math.abs(yshot) > 11) {

						if (liveBots[i].getY() >= me.getY()) {
							shotdirection = 6; // Enemy Robot below.

						} else if (liveBots[i].getY() <= me.getY()) {
							shotdirection = 5; // Enemy Robot above.
						}
					}
				}
			}
		}
		return shotdirection;
	}

	public int moveAway(BotInfo me, BotInfo[] deadBots) {
		for (int i = 0; i < deadBots.length; i++) {
			double xshot = (me.getX() + Bot.RADIUS - deadBots[i].getX());
			double yshot = (me.getY() + Bot.RADIUS - deadBots[i].getY());
			double d = Math.abs(me.getX() - deadBots[i].getX()) + Math.abs(me.getY() - deadBots[i].getY());
			if (d < 100) { // warn if within 100 pixels

				if (Math.abs(yshot) <= 10 && Math.abs(xshot) > 11) {
					if (me.getX() <= deadBots[i].getX()) {
						movement = 3; // Enemy Robot to the left.

					} else if (me.getX() >= deadBots[i].getX()) {
						movement = 4; // Enemy Robot to the right.
					}
				} else if (Math.abs(xshot) <= 10 && Math.abs(yshot) > 11) {

					if (deadBots[i].getY() >= me.getY()) {
						movement = 1; // Enemy Robot above.

					} else if (deadBots[i].getY() <= me.getY()) {
						movement = 2; // Enemy Robot below.
					}
				}
			}
		}

		return movement;
	}

	/**
	 * Method for moving away from boundaries.
	 *
	 */
	public int avoidBoundries(BotInfo me) {
		int movement = 0;
		double yrobot = me.getY();
		double xrobot = me.getX();
		if (yrobot <= 100) {
			movement = 2; // Too close to top of panel, move down.
		} else if (yrobot >= 400) {
			movement = 1; // Too close to bottom of panel, move up.
		}
		if (xrobot <= 100) {
			movement = 4; // Too close to left of panel, move right.
		} else if (xrobot >= 600) {
			movement = 3; // Too close to right of panel, move left.
		}
		return movement;
	}

	/**
	 * Method for moving away from enemy robots. Standard movement for Robot
	 * when it is safe from harm. Moves away from any enemy robots that are
	 * close to the Robot.
	 */
	public int avoidEnemyRobots(BotInfo me, BotInfo[] liveBots) {
		int movement = 0;
		double dangerousrobot = 201;
		for (int i = 0; i < liveBots.length; i++) {
			if (liveBots[i].getBulletsLeft() > 0) {
				double yrobot = (me.getY() + Bot.RADIUS - liveBots[i].getY());
				double xrobot = (me.getX() + Bot.RADIUS - liveBots[i].getX());
				double b = Math.abs(xrobot) + Math.abs(yrobot);
				if (b < 100) { // warn if within 200 pixels
					if (b < dangerousrobot) { // When this robot is closer than
												// previously checked robots.
						dangerousrobot = b;
						if (100 > Math.abs(yrobot)) {
							if (0 <= yrobot && yrobot <= 100) {
								movement = 2; // Enemy Robot above bot.
							} else if (0 > yrobot && yrobot >= -100) {
								movement = 1; // Enemy Robot below bot.
							}
						}
						if (Math.abs(xrobot) < 100) {
							if (0 <= xrobot && xrobot <= 100) {
								movement = 4; // Enemy Robot left of bot.
							} else if (0 > xrobot && xrobot >= -100) {
								movement = 3; // Enemy Robot right of bot.
							}
						}
					}
				}
			}
		}
		return movement;
	}

	/**
	 * Method for moving away from enemy robots. Standard movement for Robot
	 * when it is safe from harm. Moves away from any enemy robots that are
	 * close to the Robot.
	 */
	public int engageEnemyRobots(BotInfo me, BotInfo[] liveBots) {
		int movement = 0;
		double dangerousrobot = 1001;
		for (int i = 0; i < liveBots.length; i++) {
			if (liveBots[i].getBulletsLeft() > 0) {
				double yrobot = (me.getY() + Bot.RADIUS - liveBots[i].getY());
				double xrobot = (me.getX() + Bot.RADIUS - liveBots[i].getX());
				double b = Math.abs(xrobot) + Math.abs(yrobot);
				if (b < 1000 && b > 50) { // warn if within 1000 pixels
					if (b < dangerousrobot) { // When this robot is closer than
												// previously checked robots.
						dangerousrobot = b;
						if (1000 > Math.abs(yrobot)) {
							if (0 <= yrobot && yrobot <= 1000) {
								movement = 1; // Enemy Robot above bot.
							} else if (0 > yrobot && yrobot >= -1000) {
								movement = 2; // Enemy Robot below bot.
							}
						}
						if (Math.abs(xrobot) < 1000) {
							if (0 <= xrobot && xrobot <= 1000) {
								movement = 3; // Enemy Robot left of bot.
							} else if (0 > xrobot && xrobot >= -1000) {
								movement = 4; // Enemy Robot right of bot.
							}
						}
					}
				}
			}
		}
		return movement;
	}

	/*
	 * deadPoints - A Testing deadPoints : Up deadPoints : Left deadPoints - A
	 * Testing deadPoints : Up deadPoints : Right deadPoints - A Testing
	 * deadPoints : Up deadPoints : Left deadPoints - A Testing deadPoints : Up
	 * deadPoints : Right
	 */
	public int deadPoints(BotInfo me, BotInfo[] deadBots) {
		int movement = 0;
		for (int i = 0; i < deadBots.length; i++) {
			double yrobot = (me.getY() + Bot.RADIUS - deadBots[i].getY());
			double xrobot = (me.getX() + Bot.RADIUS - deadBots[i].getX());
			double x = 0;
			double b = Math.abs(xrobot) + Math.abs(yrobot);
			if (me.getBulletsLeft() <= 4) {
				x = 600;
			} else {
				x = 150;
			}
			
			if (deadBots[i].getBulletsLeft() > 0) {
				if (b < x) {
					x = b;

					if (100 > Math.abs(yrobot) && Math.abs(xrobot) > 150) {
						if (0 <= yrobot && yrobot <= 100) {
							System.out.println("deadPoints : Up");
							movement = 1; // Enemy Robot above bot.
						} else if (0 > yrobot && yrobot >= -100) {
							System.out.println("deadPoints : Down");
							movement = 2; // Enemy Robot below bot.

						}
					}
					if (Math.abs(xrobot) < 100 && Math.abs(yrobot) > 150) {
						if (0 <= xrobot && xrobot <= 100) {
							System.out.println("deadPoints : Left");
							movement = 3; // Enemy Robot left of bot.
						} else if (0 > xrobot && xrobot >= -100) {
							System.out.println("deadPoints : Right");
							movement = 4; // Enemy Robot right of bot.
						}
					}

				}
			}

		}

		return movement;
	}

	/**
	 * Construct and return my name
	 */
	public String getName() {
		if (name == null)
			name = "TheBest" + (botNumber < 10 ? "0" : "") + botNumber;
		return name;
	}

	/**
	 * Team Arena!
	 */
	public String getTeamName() {
		return "Ya Boi's Alliance";
	}

	/**
	 * Pick a random starting direction
	 */
	public void newRound() {
		current = standard;
	}

	/**
	 * Image names
	 */
	public String[] imageNames() {
		String[] images = { "illuminati.png", "illuminaticlose.png" };
		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images) {
		if (images != null) {
			current = standard = images[0];
			blink = images[1];
		}
	}

	/**
	 * Send my next message and clear out my message buffer
	 */
	public String outgoingMessage() {
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Required abstract method
	 */
	public void incomingMessage(int botNum, String msg) {

	}

}