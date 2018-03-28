package sagsaguz.brillavedicmaths.utils

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@DynamoDBTable(tableName = "your table name as in dynamo db")
class BrillaVMUsersDO {
    @get:DynamoDBHashKey(attributeName = "phone")
    @get:DynamoDBAttribute(attributeName = "phone")
    var phone: String? = null
        private set
    @get:DynamoDBRangeKey(attributeName = "email")
    @get:DynamoDBAttribute(attributeName = "email")
    var email: String? = null
        private set
    @get:DynamoDBAttribute(attributeName = "age")
    var age: String? = null
        private set
    @get:DynamoDBAttribute(attributeName = "city")
    var city: String? = null
        private set
    @get:DynamoDBAttribute(attributeName = "name")
    var name: String? = null
        private set
    @get:DynamoDBAttribute(attributeName = "password")
    var password: String? = null
        private set
    @get:DynamoDBAttribute(attributeName = "category")
    var category: String? = null
        private set

    fun phone(_phone: String) {
        this.phone = _phone
    }

    fun email(_email: String) {
        this.email = _email
    }

    fun age(_age: String) {
        this.age = _age
    }

    fun city(_city: String) {
        this.city = _city
    }

    fun name(_name: String) {
        this.name = _name
    }

    fun password(_password: String) {
        this.password = _password
    }

    fun category(_category: String) {
        this.category = _category
    }

}
