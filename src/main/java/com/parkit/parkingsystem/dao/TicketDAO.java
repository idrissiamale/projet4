package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * This class is responsible to get ticket data from our MySQL database.
 */
public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * It saves the user ticket in our database.
     *
     * @param ticket, the ticket with the information (ID, parking number, vehicle registration number, price, in time, out time)
     *                of the user of the parking lot.
     * @return true if the ticket is saved in the database.
     * @see DBConstants
     * @see DataBaseConfig
     */
    public boolean saveTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
            ps.execute();
            ps.close();
            return true;
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * Getting a ticket saved in database with the vehicle registration number of the user.
     *
     * @param vehicleRegNumber, the user's vehicle registration number.
     * @return a ticket.
     * @see DBConstants
     * @see DataBaseConfig
     */
    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    /**
     * Update of the user ticket with the exit time and price.
     *
     * @param ticket, the user ticket updated with exit time and parking price information.
     * @return true if the ticket is updated.
     * @see DBConstants
     * @see DataBaseConfig
     */
    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3, ticket.getId());
            ps.execute();
            ps.close();
            return true;
        } catch (Exception ex) {
            logger.error("Error saving ticket info", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * Counting the vehicle registration number entered by the user to determine whether he is a new or a recurring user
     * based on his number of visits.
     *
     * @param vehicleRegNumber, the user's vehicle registration number.
     * @return count, the number of visits of a specific user. It it's greater than zero then the user is a recurring user (he is saved in our database).
     * @see DBConstants
     * @see DataBaseConfig
     */
    public int countVehicleRegNumber(String vehicleRegNumber) {
        Connection con = null;
        int count = 0;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.COUNT);
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            ps.close();
            return count;
        } catch (Exception ex) {
            logger.error("Error counting vehicle registration numbers", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return 0;
    }
}
