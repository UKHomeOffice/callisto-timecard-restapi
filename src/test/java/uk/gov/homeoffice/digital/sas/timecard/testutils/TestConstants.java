package uk.gov.homeoffice.digital.sas.timecard.testutils;

public class TestConstants {

  public static final String KAFKA_TRANSACTION_CREATE_INITIALIZED =
      "Kafka Transaction [ create ] Initialized with message key [ %s ]";
  public static final String DATABASE_TRANSACTION_CREATE_WAS_SUCCESSFUL =
      "Database transaction [ create ] was successful";

  public static final String TRANSACTION_SUCCESSFUL_WITH_MESSAGE_KEY =
      "Transaction successful with messageKey [ %s ]";

  public static final String DATABASE_TRANSACTION_UPDATE_WAS_SUCCESSFUL =
      "Database transaction [ update ] with entity id [ %s ] was successful";

  public static final String KAFKA_TRANSACTION_UPDATE_INITIALIZED =
      "Kafka Transaction [ update ] Initialized with message key [ %s ]";

  public static final String DATABASE_TRANSACTION_DELETE_WAS_SUCCESSFUL =
      "Database transaction [ delete ] with entity id [ %s ] was successful";
  public static final String KAFKA_TRANSACTION_DELETE_INITIALIZED =
      "Kafka Transaction [ delete ] Initialized with message key [ %s ]";

  public static final String MESSAGE_FAILED_SENDING_TO_TOPIC =
      "Message with key [ %s ] failed sending to topic [ callisto-timecard ] action [ %s ]";

  public static final String MESSAGE_SENT_TO_TOPIC_CALLISTO =
      "Message with key [ %s ] sent to topic [ callisto-timecard ] with action [ %s ]";
}
