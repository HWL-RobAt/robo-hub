package de.hu_berlin.informatik.ev3.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 16.05.16.
 */
public class BlackBoard {

  /*
   * maps unique id to Object
   */
  HashMap<String, Object> dataUID = null;

  /*
   * maps name to List<Object>
   */
  HashMap<String, List<Object>> dataName = null;

  public BlackBoard() {
    this.dataUID = new HashMap<String, Object>();
    this.dataName = new HashMap<String, List<Object>>();
  }

  /*
   * Set data for name
   *
   * returns a uid (int)
   */
  public int setData(String name, Object obj) {
    List<Object> ol = getDataList(name);

    ol.add(obj);
    dataUID.put(name + (dataName.size()-1),obj);

    return (dataUID.size()-1);
  }

  public void setData(String name, int uid, Object obj) {
    String keyUID = name + uid;

    List<Object> ol = getDataList(name);

    if (dataUID.containsKey(keyUID)) {
      /*
       * clear old data in list
       */
      ol.remove(dataUID.get(keyUID));
    }

    ol.add(obj);

    dataUID.put(keyUID,obj);
  }

  public Object getData(String name, int uid) {
    String keyUID = name + uid;
    if (!dataUID.containsKey(keyUID)) return null;
    return dataUID.get(name);
  }

  public List<Object> getDataList(String name) {
    if ( ! dataName.containsKey(name) ) {
      List<Object> new_ol= new ArrayList<Object>();
      dataName.put(name, new_ol);
    }
    return dataName.get(name);
  }

  public Object getDataByName(String name, int index) {
    if (! dataName.containsKey(name)) return null;

    List<Object> ol4Name = dataName.get(name);

    if ( ol4Name.size() <= index ) return null;

    return ol4Name.get(index);
  }


}
