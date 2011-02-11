package net.gryszko.spock.dao

import net.gryszko.spock.model.Bank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.unitils.dbunit.annotation.DataSet
import spock.lang.Specification
import spock.unitils.UnitilsSupport

@ContextConfiguration(locations = ["classpath:/resources.xml"])
@UnitilsSupport
@DataSet
class BankDaoSpec extends Specification {

  @Autowired
  private BankDao dao

  @Transactional
  def "finds a bank by name"() {
    setup:
    def bankName = 'MBank'

    when:
    Bank bank = dao.findByName(bankName)

    then:
    bank.name == bankName
  }
}
