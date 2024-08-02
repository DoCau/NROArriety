/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arriety.MaQuaTang;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 *
 * @author Administrator
 */
public class MaQuaTang {
    int id;
    String code;
    int countLeft;
    public HashMap<Integer, Integer> detail = new HashMap<>();
    public ArrayList<Integer> listIdPlayer = new ArrayList<>();
    public ArrayList<ItemOption> option = new ArrayList<>();
    Timestamp datecreate;
    Timestamp dateexpired;
    JSONArray dataArray;

    public boolean isUsedGiftCode(int idPlayer, int idGiftcode) {
        boolean isUsed = false;
        try (Connection con = GirlkunDB.getConnection();) {
            PreparedStatement ps = con.prepareStatement("select list_gift_code_id from player where id = ?");
            ps.setInt(1, idPlayer);
            ResultSet rs = ps.executeQuery();
            rs.next();
            dataArray = (JSONArray) JSONValue.parse(rs.getString("list_gift_code_id"));
            if (dataArray.size() == 0) {
                dataArray.clear();
                isUsed = false;
            } else {
                for (int i = 0; i < dataArray.size(); i++) {
                    if (Integer.parseInt(dataArray.get(i).toString()) == idGiftcode) {
                        isUsed = true;
                        dataArray.clear();
                        break;
                    }
                }
            }
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isUsed;
    }

    public void addPlayerUsed(int idPlayer, int idGiftcode) {

        listIdPlayer.add(idPlayer);

        try (Connection con = GirlkunDB.getConnection();) {
            PreparedStatement ps = con.prepareStatement("select list_gift_code_id from player where id = ?");
            ps.setInt(1, idPlayer);
            ResultSet rs = ps.executeQuery();
            rs.next();
            dataArray = (JSONArray) JSONValue.parse(rs.getString("list_gift_code_id"));
            dataArray.add(idGiftcode);
            ps = con.prepareStatement("update player set list_gift_code_id = ? where id = ?");
            ps.setString(1, String.valueOf(dataArray));
            ps.setInt(2, idPlayer);
            ps.executeUpdate();
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean timeCode() {
        return this.datecreate.getTime() > this.dateexpired.getTime() ? true : false;
    }
}
