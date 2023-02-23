package uk.gov.homeoffice.digital.sas.timecard.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.DATABASE_TRANSACTION_CREATE_WAS_SUCCESSFUL;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.DATABASE_TRANSACTION_DELETE_WAS_SUCCESSFUL;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.DATABASE_TRANSACTION_UPDATE_WAS_SUCCESSFUL;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.KAFKA_TRANSACTION_CREATE_INITIALIZED;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.KAFKA_TRANSACTION_DELETE_INITIALIZED;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.KAFKA_TRANSACTION_UPDATE_INITIALIZED;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.TRANSACTION_SUCCESSFUL_WITH_MESSAGE_KEY;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;
import uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory;

@SpringBootTest
@DirtiesContext
@WebAppConfiguration
@AutoConfigureMockMvc(addFilters = true)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092",
    "port=9092"})
class KafkaEntityListenerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private TimeEntry timeEntry;

  private UUID tenantId;

  private UUID ownerId;

  private String messageKey;

  @BeforeEach
  void setUp() {
    ownerId = UUID.randomUUID();
    timeEntry = TimeEntryFactory.createTimeEntry(ownerId,
        CommonUtils.getAsDate(LocalDateTime.now()));
    tenantId = timeEntry.getTenantId();
    messageKey = CommonUtils.generateMessageKey(timeEntry);
  }

  @Test
  void givenValidRequest_WhenSendingCreateRequest_thenTransactionSyncLogsSuccessMessage()
      throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    persistTimeEntry(timeEntry);
    TransactionSynchronizationManager.initSynchronization();

    assertThat(String.format(
        KAFKA_TRANSACTION_CREATE_INITIALIZED,
        messageKey)).isEqualTo(logList.get(0).getMessage());

    assertThat(logList.get(1).getMessage()).isEqualTo(DATABASE_TRANSACTION_CREATE_WAS_SUCCESSFUL);
    assertThat(logList.get(2).getMessage()).isEqualTo(String.format(
        TRANSACTION_SUCCESSFUL_WITH_MESSAGE_KEY, messageKey));

  }

  @Test
  void givenValidRequest_WhenSendingUpdateRequest_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    String id = persistTimeEntry(timeEntry);
    TransactionSynchronizationManager.initSynchronization();

    TimeEntry timeEntryUpdate = TimeEntryFactory.createTimeEntry(ownerId, tenantId,
        CommonUtils.getAsDate(LocalDateTime.now().plusHours(1)));

    mockMvc.perform(put("/resources/time-entries/"+ id + "?tenantId=" + tenantId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(CommonUtils.objectAsJsonString(timeEntryUpdate)))
        .andDo(print())
        .andExpect(status().isOk());


    List<ILoggingEvent> filteredList =
        logList.stream().filter(o -> o.getMessage().equals(
            String.format(
                DATABASE_TRANSACTION_UPDATE_WAS_SUCCESSFUL, id))).toList();

    assertThat(filteredList).hasSize(1);

    assertThat(logList.get(3).getMessage()).isEqualTo(String.format(
        KAFKA_TRANSACTION_UPDATE_INITIALIZED,
        messageKey));

    assertThat(logList.get(4).getMessage()).isEqualTo(String.format(
        DATABASE_TRANSACTION_UPDATE_WAS_SUCCESSFUL,
        id));

    assertThat(logList.get(5).getMessage()).isEqualTo(String.format(
        TRANSACTION_SUCCESSFUL_WITH_MESSAGE_KEY, messageKey));
  }

  @Test
  void givenValidRequest_WhenSendingDelete_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    String id = persistTimeEntry(timeEntry);
    TransactionSynchronizationManager.initSynchronization();

    mockMvc.perform(delete("/resources/time-entries/"+ id + "?tenantId=" + tenantId))
        .andDo(print())
        .andExpect(status().isOk());

    List<ILoggingEvent> filteredList =
        logList.stream().filter(o -> o.getMessage().equals(
            String.format(
                DATABASE_TRANSACTION_DELETE_WAS_SUCCESSFUL, id))).toList();

    assertThat(filteredList).hasSize(1);

    assertThat(logList.get(3).getMessage() ).isEqualTo(String.format(
        KAFKA_TRANSACTION_DELETE_INITIALIZED, messageKey));

    assertThat(logList.get(4).getMessage()).isEqualTo(String.format(
        DATABASE_TRANSACTION_DELETE_WAS_SUCCESSFUL,
        id));

    assertThat(logList.get(5).getMessage()).isEqualTo(String.format(
      TRANSACTION_SUCCESSFUL_WITH_MESSAGE_KEY, messageKey));
  }

  private String persistTimeEntry(TimeEntry timeEntry) throws Exception {
    MvcResult mvcResult = mockMvc.perform(post("/resources/time-entries?tenantId=" + tenantId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(CommonUtils.objectAsJsonString(timeEntry)))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    Map<String, List<LinkedHashMap<String, String>>> apiResponseMap =
            new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Map.class);
    assertThat(apiResponseMap.get("items")).isNotEmpty();
    return apiResponseMap.get("items").get(0).get("id");
  }

  @NotNull
  private static ListAppender<ILoggingEvent> getLoggingEventListAppender() {
    Logger kafkaLogger = (Logger) LoggerFactory.getLogger(KafkaDbTransactionSynchronizer.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    kafkaLogger.addAppender(listAppender);
    return listAppender;
  }

  @AfterEach
  void clear() {
    TransactionSynchronizationManager.clear();
  }

}
