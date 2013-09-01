package rewards.internal.restaurant;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 * Finds restaurants using the Hibernate API.
 */
@Repository
public class HibernateRestaurantRepository implements RestaurantRepository {

	private SessionFactory sessionFactory;

	/**
	 * Creates an new hibernate-based restaurant repository.
	 * @param sessionFactory the Hibernate session factory required to obtain sessions
	 */
	public HibernateRestaurantRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


    /**
     * Find the restaurant for supplied merchantNumber
     *
     * @param merchantNumber the merchant number
     * @return restaurant, or null if not found
     */
    @Override
	public Restaurant findByMerchantNumber(String merchantNumber) {
		return (Restaurant) getCurrentSession().createQuery("from Restaurant r where r.number = :merchantNumber")
				.setString("merchantNumber", merchantNumber).uniqueResult();
	}

	/**
	 * Returns the session associated with the ongoing reward transaction.
	 * @return the transactional session
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}