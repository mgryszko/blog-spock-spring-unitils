package net.gryszko.spock.dao

import net.gryszko.spock.model.Bank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.unitils.dbunit.annotation.DataSet
import spock.lang.Specification
import spock.unitils.UnitilsSupport
import javax.annotation.Resource
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import org.springframework.jdbc.core.simple.ParameterizedRowMapper
import java.sql.ResultSet
import groovy.sql.Sql
import spock.lang.Unroll

// Groovy mixin to not pollute production classes with test-specific stuff
@Category(Bank)
class BankExtensions {
  boolean equals(Object other) {
    return (this.id == other.id) && (this.name == other.name) &&
            (this.description == other.description)
  }
}

@ContextConfiguration(locations = ["classpath:/resources.xml", "classpath:/test-resources.xml"])
@UnitilsSupport
@DataSet
class BankDaoSpec extends Specification {

  private static final String SELECT_ALL_FROM_BANK =
'''select id as "id", name as "name", description as "description"
from bank'''

  @Autowired
  private BankDao dao

  @Resource
  private SimpleJdbcTemplate jdbcTemplate

  @Resource
  private Sql sql

  @Transactional
  def "finds a bank by name"() {
    setup:
    def bankName = 'MBank'

    when:
    Bank bank = dao.findByName(bankName)

    then:
    bank.name == bankName
  }

  @Transactional
  @Unroll("finds a bank by name #bankName (data-driven test)")
  def "finds a bank by name (data-driven test)"() {
    expect:
    Bank bank = dao.findByName(bankName)
    bank.name == bankName

    where:
    bankName << ['BPH', 'BOS', 'HSBC', 'INGBS', 'MBank', 'PKOBP']
  }

  def "saves a bank"() {
    setup: "a bank"
    def bank = new Bank(name: "Santander", description: "Banco Santander")

    when: "it persists a bank"
    dao.save(bank)

    then: "id is automatically assigned"
    bank.id != null

    and: "we check if it has been really persisted - Spring way"
    def savedBank = jdbcTemplate.queryForObject("${SELECT_ALL_FROM_BANK} where id = ?", new ParameterizedRowMapper<Bank>() {
      Bank mapRow(ResultSet rs, int rowNum) {
        return new Bank(id: rs.getLong('id'),
                name: rs.getString('name'),
                description: rs.getString('description'))
      }
    }, bank.id)

    use(BankExtensions) {
      savedBank == bank
    }

    and: "we check if it has been really persisted - Groovy way"
    sql.eachRow(SELECT_ALL_FROM_BANK + " where id = ${bank.id}") {
      savedBank = new Bank(it.toRowResult())
    }

    use(BankExtensions) {
      savedBank == bank
    }
  }
}
