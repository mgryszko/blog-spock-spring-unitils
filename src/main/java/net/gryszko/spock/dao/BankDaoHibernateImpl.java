package net.gryszko.spock.dao;

import net.gryszko.spock.model.Bank;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class BankDaoHibernateImpl extends HibernateDaoSupport implements BankDao {

	@Override
	public Bank findByName(String name) {
        DetachedCriteria crit = DetachedCriteria.forClass(Bank.class);
        crit.add(Restrictions.eq("name", name));
        List<Bank> results = getHibernateTemplate().findByCriteria(crit);
        return DataAccessUtils.uniqueResult(results);
	}

    @Override
    public void save(Bank bank) {
        getHibernateTemplate().save(bank);
    }
}
