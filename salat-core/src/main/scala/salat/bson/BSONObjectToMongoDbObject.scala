/*
 * Copyright (c) 2010 - 2015 Novus Partners, Inc. (http://www.novus.com)
 * Copyright (c) 2015 - 2016 Rose Toomey (https://github.com/rktoomey) and other individual contributors where noted
 *
 * Module:        salat-core
 * Class:         BSONObjectToMongoDbObject.scala
 * Last modified: 2016-07-10 23:49:08 EDT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *           Project:  http://github.com/salat/salat
 *              Wiki:  http://github.com/salat/salat/wiki
 *             Slack:  https://scala-salat.slack.com
 *      Mailing list:  http://groups.google.com/group/scala-salat
 *     StackOverflow:  http://stackoverflow.com/questions/tagged/salat
 *
 */

package salat.bson

import com.mongodb.casbah.Imports._
import org.bson._
import org.bson.types.BasicBSONList
import scala.jdk.javaapi.CollectionConverters._

object BSONObjectToMongoDbObject {

  def apply(bo: BSONObject): MongoDBObject = {
    val map = asScala(bo.asInstanceOf[BasicBSONObject])
    val builder = MongoDBObject.newBuilder
    map.foreach {
      case (k, v) =>

        builder += k -> transform(v)
    }
    builder.result()
  }

  def transform(v: Any): AnyRef = v match {
    case bl: BasicBSONList => {
      val builder = MongoDBList.newBuilder
      val iter = bl.iterator()
      while (iter.hasNext) {
        builder += transform(iter.next())
      }
      builder.result()
    }
    case bo: BasicBSONObject => {
      val map = asScala(bo)
      val builder = MongoDBObject.newBuilder
      map.foreach {
        case (k, v) =>
          builder += k -> transform(v)
      }
      builder.result()
    }
    case x => x.asInstanceOf[AnyRef]
  }

}
