package uk.gov.homeoffice.digital.sas.timecard.listeners;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaDbTransactionSynchronizer;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;
import uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory;

@SpringBootTest
@DirtiesContext
@WebAppConfiguration
@AutoConfigureMockMvc(addFilters = true)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092",
    "port=9092" })
class KafkaEntityListenerIT<T> {

  @Autowired
  private MockMvc mockMvc;

  private TimeEntry timeEntry;

  @MockBean
  private KafkaProducerService<T> kafkaProducerService;

  @MockBean
  private KafkaEntityListener<T> kafkaEntityListener;

  @MockBean
  private TransactionSynchronization sync;

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
    TransactionSynchronizationManager.initSynchronization();
    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  void givenValidRequest_WhenSendingCreateRequest_thenTransactionSyncLogsSuccessMessage()
      throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    persistTimeEntry(timeEntry);

    assertEquals(String.format(
        "Kafka Transaction [ create ] Initialized with message key [ %s ]",
        messageKey), logList.get(0).getMessage());

    assertEquals("Database transaction [ create ] was successful", logList.get(1).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(2).getMessage());
  }

  @Test
  void givenValidRequest_WhenSendingUpdateRequest_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    String id = persistTimeEntry(timeEntry);

    TimeEntry timeEntryUpdate = TimeEntryFactory.createTimeEntry(ownerId, tenantId,
        CommonUtils.getAsDate(LocalDateTime.now().plusHours(1)));

    mockMvc.perform(put("/resources/time-entries/"+ id + "?tenantId=" + tenantId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(CommonUtils.timeEntryAsJsonString(timeEntryUpdate)))
        .andDo(print())
        .andExpect(status().isOk());

    assertEquals(String.format(
        "Kafka Transaction [ update ] Initialized with message key [ %s ]",
        messageKey), logList.get(3).getMessage());

    assertEquals(String.format(
        "Database transaction [ update ] with id [ %s ] was successful",
            id), logList.get(4).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(5).getMessage());
  }

  @Test
  void givenValidRequest_WhenSendingDelete_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    String id = persistTimeEntry(timeEntry);

    mockMvc.perform(delete("/resources/time-entries/"+ id + "?tenantId=" + tenantId))
        .andDo(print())
        .andExpect(status().isOk());

    assertEquals(String.format(
        "Kafka Transaction [ delete ] Initialized with message key [ %s ]",
        messageKey), logList.get(3).getMessage());

    assertEquals(String.format(
        "Database transaction [ delete ] with id [ %s ] was successful",
            id), logList.get(4).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(5).getMessage());
  }

  private String persistTimeEntry(TimeEntry timeEntry) throws Exception {
    MvcResult mvcResult = mockMvc.perform(post("/resources/time-entries?tenantId=" + tenantId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(CommonUtils.timeEntryAsJsonString(timeEntry)))
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
