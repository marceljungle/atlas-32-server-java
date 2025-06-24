let map;
const markers = {};
const infoWins = {};

// initMap ONLY initializes the map.
function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 40.4167, lng: -3.7033 },
    zoom: 5
  });

  devices.forEach(d => {
    const marker = new google.maps.Marker({
      position: { lat: 40.4167, lng: -3.7033 },
      map: map,
      label: {
        text: d.name,
        fontSize: '12px',
        fontWeight: 'bold'
      },
      title: d.name
    });
    const iw = new google.maps.InfoWindow({ content: `<strong>${d.name}</strong>` });
    marker.addListener("click", () => iw.open({ anchor: marker, map, shouldFocus: false }));

    markers[d.mqttDeviceId] = marker;
    infoWins[d.mqttDeviceId] = iw;
  });

  updateAllLocations();
  setInterval(updateAllLocations, 5000);
}

function updateAllLocations() {
  devices.forEach(d => {
    fetch(`/api/device/${d.mqttDeviceId}/location`)
    .then(r => r.ok ? r.json() : null)
    .then(data => {
      if (data && data.latitude && data.longitude) {
        const pos = { lat: data.latitude, lng: data.longitude };
        markers[d.mqttDeviceId].setPosition(pos);
      }
    });
  });
}

function sendCommandToDevice(deviceId, command, element = null) {
  const apiUrl = `/api/device/${deviceId}/command`;
  const requestBody = { command: command };

  console.log(`Sending command to ${deviceId}:`, requestBody);

  fetch(apiUrl, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(requestBody)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`API responded with status ${response.status}`);
    }
    console.log(`Command '${command}' sent successfully to ${deviceId}.`);
  })
  .catch(error => {
    console.error(`Error sending command '${command}' to device ${deviceId}:`, error);
    if (element && element.type === 'checkbox') {
      console.warn(`Reverting switch state for ${deviceId}.`);
      element.checked = !element.checked;
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".btn-request").forEach(btn => {
    btn.addEventListener("click", e => {
      const deviceId = e.target.dataset.id;
      sendCommandToDevice(deviceId, "GET_COORDINATES");
    });
  });

  document.querySelectorAll(".live-switch").forEach(liveSwitch => {
    liveSwitch.addEventListener("change", e => {
      const deviceId = e.target.dataset.id;
      const isEnabled = e.target.checked;
      const command = isEnabled ? "LIVE_ON" : "LIVE_OFF";
      sendCommandToDevice(deviceId, command, e.target);
    });
  });
});