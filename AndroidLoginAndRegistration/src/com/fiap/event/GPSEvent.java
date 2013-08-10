
package com.fiap.event;

import android.location.Location;

public interface GPSEvent
{
	public abstract void onGPSUpdate(Location location);
}
