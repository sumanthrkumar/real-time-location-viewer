from dataclasses import dataclass
from datetime import datetime, timezone
import json

@dataclass
class LocationUpdate:
    device_id: str
    latitude: float
    longitude: float
    timestamp: datetime

    def to_dict(self):
        return {"deviceId": self.device_id, 
                "latitude": self.latitude, 
                "longitude": self.longitude, 
                "timestamp": self.timestamp.replace(tzinfo=timezone.utc).isoformat()
                }