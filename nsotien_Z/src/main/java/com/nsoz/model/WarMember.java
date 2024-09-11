
package com.nsoz.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.nsoz.db.mongodb.MongoDbConnection;
import com.nsoz.server.Config;
import java.time.LocalDateTime;
import java.util.Calendar;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author Admin
 */
public class WarMember {

    public int id;
    public String name;
    public int point;
    public byte faction;
    public byte type;

    public WarMember clone() {
        WarMember mem = new WarMember();
        mem.id = this.id;
        mem.name = this.name;
        mem.point = this.point;
        mem.faction = this.faction;
        mem.type = this.type;
        return mem;
    }

    public String getRank() {
        String result;
        if (this.point >= 4000) {
            result = "Nhẫn Giả";
        } else if (this.point >= 1500) {
            result = "Thượng Nhẫn";
        } else if (this.point >= 600) {
            result = "Trung Nhẫn";
        } else if (this.point >= 200) {
            result = "Hạ Nhẫn";
        } else {
            result = "Học Giả";
        }
        return result;
    }

    public void save() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        Document dcm = new Document();
        dcm.append("player_id", id);
        dcm.append("name", name);
        dcm.append("point", point);
        dcm.append("server_id", Config.getInstance().getServerID());
        dcm.append("week", weekOfYear);
        dcm.append("month", month);
        dcm.append("year", year);
        MongoCollection collection = MongoDbConnection.getCollection("top_war");
        Bson filter = Filters.and(Filters.eq("type", type), Filters.eq("player_id", id), Filters.eq("server_id", Config.getInstance().getServerID()), Filters.eq("week", weekOfYear), Filters.eq("month", month), Filters.eq("year", year));
        Document update = new Document("$set", dcm);
        UpdateOptions options = new UpdateOptions().upsert(true);
        collection.updateOne(filter, update, options);

    }

}
