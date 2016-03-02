package datasources


import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class MultipleDataSourceSpec extends Specification {

    void "Test Audience to default data source"() {
        when:
        new Audience(name: "Something").save(flush: true)
        then:
        Audience.count() == 1
    }

    // (NB: in Asi.groovy: datasources(['asi', 'DEFAULT']).)
    void "Test Asi to default source"() {
        when:
        new Asi(asiCode:"foo", asiName:"bar").save(flush:true)
        then:
        Asi.count() == 1
    }

    // FIXME
    // Works with 3.0.2.BUILD-SNAPSHOT, 3.0.2.
    // (Starts to) fail(s) with: 3.1.1, 3.1.2, and 3.2.0.BUILD-SNAPSHOT
    // Then the ".asi." directive fails with "No such property: asi".
    // (NB: in Asi.groovy: datasources(['asi', 'DEFAULT']).)
    void "Test Asi to asi data source "() {
        when:
        new Asi(asiCode:"foo", asiName:"bar").asi.save(flush:true)
        then:
        Asi.asi.count() == 1
    }

  @Ignore
  void "Test multiple data sources"() {
    when: 
      new Audience(name: "Something").save(flush: true)
      new Asi(asiCode:"foo", asiName:"bar").save(flush:true)
      def asi = Asi.get(1)
      def url = Asi.withSession { session ->
      		session.connection().metaData.URL
      }
    then:
      Audience.count() == 1
      asi != null
      url == "jdbc:h2:asiDb"
  }
}
