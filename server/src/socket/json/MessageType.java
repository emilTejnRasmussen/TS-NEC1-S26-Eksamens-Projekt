package socket.json;

public enum MessageType
{
  REGISTER,
  HEARTBEAT,
  CAR_PARKED,
  CAR_LEFT,

  ACK,
  ERROR,
  SET_LIGHT,
  UPDATE_TOTAL,
  SYNC_STATE,

  BROADCAST
}