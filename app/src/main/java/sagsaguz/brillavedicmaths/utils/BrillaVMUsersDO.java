package sagsaguz.brillavedicmaths.utils;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "brillavm-mobilehub-86056955-BrillaVMUsers")

public class BrillaVMUsersDO {
    private String _phone;
    private String _email;
    private String _age;
    private String _city;
    private String _name;

    @DynamoDBHashKey(attributeName = "phone")
    @DynamoDBAttribute(attributeName = "phone")
    public String getPhone() {
        return _phone;
    }

    public void phone(final String _phone) {
        this._phone = _phone;
    }
    @DynamoDBRangeKey(attributeName = "email")
    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return _email;
    }

    public void email(final String _email) {
        this._email = _email;
    }
    @DynamoDBAttribute(attributeName = "age")
    public String getAge() {
        return _age;
    }

    public void age(final String _age) {
        this._age = _age;
    }
    @DynamoDBAttribute(attributeName = "city")
    public String getCity() {
        return _city;
    }

    public void city(final String _city) {
        this._city = _city;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void name(final String _name) {
        this._name = _name;
    }

}
