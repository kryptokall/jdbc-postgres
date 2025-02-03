package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order " +
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

		Map<Long, Order> orders = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();
		while (rs.next()) {
			Long orderId = rs.getLong("order_id");
			if (orders.get(orderId) == null) {
				Order order = instantiateOrder(rs);
				orders.put(orderId, order);
			}
			Long productId = rs.getLong("product_id");
			if (prods.get(productId) == null) {
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}

			orders.get(orderId).getProducts().add(prods.get(productId));
		}

		for (Long orderId : orders.keySet()) {
			System.out.println(orders.get(orderId));
			for (Product p : orders.get(orderId).getProducts()) {
				System.out.println(p);
			}
			System.out.println();
		}
	}

	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setName(rs.getString("name"));
		p.setDescription(rs.getString("description"));
		p.setImgUrl(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		return p;
	}

	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order o = new Order();
		o.setId(rs.getLong("order_id"));
		o.setLatitude(rs.getDouble("latitude"));
		o.setLongitude(rs.getDouble("longitude"));
		o.setMoment(rs.getTimestamp("moment").toInstant());
		o.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return o;
	}

}
