//snippet-sourcedescription:[PutItem.java demonstrates how to put an item in a DynamoDB table.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
//snippet-sourceauthor:[soo-aws]
/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import org.w3c.dom.Attr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Put an item in a DynamoDB table.
 * <p>
 * Takes the name of the table, a name (primary key value) and a greeting
 * (associated with the key value).
 * <p>
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class PutItem {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutItem <table> <name> [field=value ...]\n\n" +
                "Where:\n" +
                "    table    - the table to put the item in.\n" +
                "    name     - a name to add to the table. If the name already\n" +
                "               exists, its entry will be updated.\n" +
                "Additional fields can be added by appending them to the end of the\n" +
                "input.\n\n" +
                "Example:\n" +
                "    PutItem Cellists Pau Language=ca Born=1876\n";

        /*if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }*/

        String table_name = "ChannelMessagesTable";
        ArrayList<String[]> extra_fields = new ArrayList<String[]>();

        // any additional args (fields to add to database)?
        for (int x = 2; x < args.length; x++) {
            String[] fields = args[x].split("=", 2);
            if (fields.length == 2) {
                extra_fields.add(fields);
            } else {
                System.out.format("Invalid argument: %s\n", args[x]);
                System.out.println(USAGE);
                System.exit(1);
            }
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.format("Adding \"%s\" to \"%s\"", timestamp, table_name);
        if (extra_fields.size() > 0) {
            //System.out.println("Additional fields:");
            for (String[] field : extra_fields) {
                System.out.format("  %s: %s\n", field[0], field[1]);
            }
        }

        HashMap<String, AttributeValue> item_values =
                new HashMap<String, AttributeValue>();

        item_values.put("timestamp", new AttributeValue(timestamp.toString()));

        /*for (String[] field : extra_fields) {
            item_values.put(field[0], new AttributeValue(field[1]));
        }*/
        item_values.put("channelmessagetext", new AttributeValue("message1"));

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();

        try {
            ddb.putItem(table_name, item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", table_name);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        //System.out.println("Done!");
    }

    public static void writeToDynamoDB(Map<String, String> item_values) {
        String table_name = "ChannelMessagesTable";
        ArrayList<String[]> extra_fields = new ArrayList<String[]>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.format("Adding \"%s\" to \"%s\"", timestamp, table_name);

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("timestamp", new AttributeValue(timestamp.toString()));
        for (Map.Entry<String, String> e : item_values.entrySet()) {
            itemValues.put(e.getKey(), new AttributeValue(e.getValue()));
        }

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();

        try {
            ddb.putItem(table_name, itemValues);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", table_name);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        //System.out.println("Done!");
    }
}