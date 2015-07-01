package tables

import models.Fund
import slick.driver.JdbcProfile
import slick.lifted.{Tag, ProvenShape}
import slick.model.Table

class FundTable(tag: Tag) extends Table[Fund](tag, "FUND"){

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def url = column[String]("URL")

    override def * : ProvenShape[Fund] = (id, name, url) <> ((Fund.apply _).tupled, Fund.unapply _)
}

