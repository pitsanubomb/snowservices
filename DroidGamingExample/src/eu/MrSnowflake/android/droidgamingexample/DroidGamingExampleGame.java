/* GameActivityExample - This is an example for DroidGaming.
 * Copyright (C) 2008  Maarten 'MrSnowflake' Krijn
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package eu.MrSnowflake.android.droidgamingexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import eu.MrSnowflake.android.droidgaming.DroidGamingThread;
import eu.MrSnowflake.android.droidgaming.DroidGamingThread.GameEvent;
import eu.MrSnowflake.android.droidgaming.DroidGamingThread.GameListener;

public class DroidGamingExampleGame implements GameListener {
		private Context mContext;
		private Sprite mSprites[];
		
		private final static int SPEED = 10;
		private static final String TAG = "SnowSaysGame";
		
		public DroidGamingExampleGame(Context ctx) {
			mContext = ctx;
			mSprites = new Sprite[2];
			Resources res = mContext.getResources();
			mSprites[0] = new Sprite(BitmapFactory.decodeResource(res, R.drawable.snow));
			mSprites[0].y = 100;
			mSprites[1] = new Sprite(BitmapFactory.decodeResource(res, R.drawable.snow));
			mSprites[1].x = 100;
		}
		
		@Override
		public boolean onFrameEnd(DroidGamingThread parent, GameEvent event) {
			return true;
		}

		@Override
		public boolean onFrameStart(DroidGamingThread parent, GameEvent event) {
			Sprite currentSprite = mSprites[0];


			if (event.keysDown[KeyEvent.KEYCODE_DPAD_DOWN])
				currentSprite.y += SPEED / 4;
			else if (event.keysDown[KeyEvent.KEYCODE_DPAD_UP])
				currentSprite.y -= SPEED / 4;
			if (event.keysDown[KeyEvent.KEYCODE_DPAD_LEFT])
				currentSprite.x -= SPEED / 4;
			else if (event.keysDown[KeyEvent.KEYCODE_DPAD_RIGHT])
				currentSprite.x += SPEED / 4;
			
			if (currentSprite.y < 0)
				currentSprite.y = 0;
			else if (currentSprite.y >= parent.getHeight() - currentSprite.mBitmap.getHeight())
				currentSprite.y = parent.getHeight() - currentSprite.mBitmap.getHeight();
			if (currentSprite.x < 0)
				currentSprite.x = 0;
			else if (currentSprite.x >= parent.getWidth() - currentSprite.mBitmap.getWidth())
				currentSprite.x = parent.getWidth() - currentSprite.mBitmap.getWidth();

			currentSprite = mSprites[1];
			if (event.keysHeld[KeyEvent.KEYCODE_DPAD_DOWN])
				currentSprite.y += event.timeElapsed * SPEED * 10;
			else if (event.keysHeld[KeyEvent.KEYCODE_DPAD_UP])
				currentSprite.y -= event.timeElapsed * SPEED * 10;
			if (event.keysHeld[KeyEvent.KEYCODE_DPAD_LEFT])
				currentSprite.x -= event.timeElapsed * SPEED * 10;
			else if (event.keysHeld[KeyEvent.KEYCODE_DPAD_RIGHT])
				currentSprite.x += event.timeElapsed * SPEED * 10;
			
			if (currentSprite.y < 0)
				currentSprite.y = 0;
			else if (currentSprite.y >= parent.getHeight() - currentSprite.mBitmap.getHeight())
				currentSprite.y = parent.getHeight() - currentSprite.mBitmap.getHeight();
			if (currentSprite.x < 0)
				currentSprite.x = 0;
			else if (currentSprite.x >= parent.getWidth() - currentSprite.mBitmap.getWidth())
				currentSprite.x = parent.getWidth() - currentSprite.mBitmap.getWidth();

			return true;
		}

		@Override
		public boolean onRender(DroidGamingThread parent, Canvas canvas) {
			// clear canvas
			canvas.drawARGB(255, 0, 0, 20);
			
			for (Sprite sprite : mSprites) {
				canvas.drawBitmap(sprite.mBitmap, sprite.x, sprite.y, new Paint());
			}
			
			return true;
		}
	}
