package i5.las2peer.services.servicePackage.datamodel;

import i5.las2peer.services.servicePackage.statistics.Stats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 */

public class MySqlHelper {
	public static void markExpertsForEvaluation(ConnectionSource connectionSrc)
			throws SQLException {
		List<Long> reputations = new ArrayList<Long>();

		Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSrc,
				UserEntity.class);
		List<UserEntity> user_entites = userDao.queryForAll();
		for (UserEntity entity : user_entites) {
			long reputation = entity.getReputation();
			reputations.add(reputation);
		}
		
		Stats stats = new Stats(reputations);
		
		// System.out.println("MAX::" + stats.getMax());
		// System.out.println("MIN::" + stats.getMin());
		// System.out.println("MEDIAN::" + stats.getMedian());
		// System.out.println("UPPER QUARTILE::" + stats.getUpperQuartile());
		// System.out.println("LOWER QUARTILE::" + stats.getLowerQuartile());
		// System.out.println("GET ABOVE PERCENTILE::"
		// + stats.getPercentileAbove(98));

		UpdateBuilder updateBuilder = userDao.updateBuilder();
		for (UserEntity entity : user_entites) {
			long reputation = entity.getReputation();
			if (reputation >= stats.getPercentileAbove(98)) {
				updateBuilder.where().eq("userId", entity.getUserId());
				updateBuilder.updateColumnValue("probable_expert", true);
				updateBuilder.update();
			}
		}

	}

}
