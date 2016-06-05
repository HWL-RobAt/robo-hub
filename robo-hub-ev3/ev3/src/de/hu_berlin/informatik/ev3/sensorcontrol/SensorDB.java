package de.hu_berlin.informatik.ev3.sensorcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 16.05.16.
 */
public class SensorDB {
  HashMap<String, Object> sensorData = null;
  HashMap<Class, List<Object>> sensorClasses = null;

  public SensorDB() {
    this.sensorData = new HashMap<String, Object>();
    this.sensorClasses = new HashMap<Class, List<Object>>();
  }

  /*
   * Set data for name
   */
  private void setSensorData(String name, Object obj) {
    sensorData.put(name, obj);
  }

  public void setSensorData(Class c, String name, Object obj) {
    List<Object> ol4Class = getSensorClassList(c);
    ol4Class.add(obj);
    setSensorData(name, obj);
  }

  public Object getSensorData(String name) {
    return sensorData.get(name);
  }

  public List<Object> getSensorClassList(Class c) {
    if ( ! sensorClasses.containsKey(c) ) {
      List<Object> new_ol= new ArrayList<Object>();
      sensorClasses.put(c, new_ol);
    }
    return sensorClasses.get(c);
  }

  public Object getSensorDataForClass(Class c, int index) {
    if (! sensorClasses.containsKey(c)) return null;

    List<Object> ol4Class = sensorClasses.get(c);

    if ( ol4Class.size() <= index ) return null;

    return ol4Class.get(index);
  }

  public Object getSensorDataForClass(Class c) {
    return getSensorDataForClass(c, 0);
  }


}
