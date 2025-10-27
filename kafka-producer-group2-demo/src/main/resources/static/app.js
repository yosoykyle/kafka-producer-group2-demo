// jQuery document ready
$(function() {
  // loadEvents: fetches recent events from the backend and renders them in the table
  function loadEvents() {
    // GET /api/events returns an array of event objects
    $.get('/api/events').done(function(data) {
      const tbody = $('#eventsTable');
      // clear existing rows
      tbody.empty();
      // guard: ensure we got an array
      if (!Array.isArray(data)) return;
      // show newest first by reversing the array copy
      data.slice().reverse().forEach(function(evt) {
        // items may be an array or a single value; normalize for display
        const items = Array.isArray(evt.items) ? evt.items.join(', ') : evt.items;
        const row = `<tr><td>${evt.order_id || ''}</td><td>${evt.customer_name || ''}</td><td>${items || ''}</td><td>${evt.timestamp || ''}</td></tr>`;
        tbody.append(row);
      });
    }).fail(function() {
      // network or server error while fetching events
      console.error('Failed to load events');
    });
  }

  // refresh button handler: re-fetch events
  $('#refreshBtn').on('click', loadEvents);

  // order form submit handler: gather form values and POST to /api/orders
  $('#orderForm').on('submit', function(e) {
    e.preventDefault();

    // assemble payload from form fields
    const payload = {
      orderId: $('#orderId').val(),
      customerName: $('#customerName').val(),
      // items entered as comma-separated string -> convert to array of trimmed strings
      items: $('#items').val() ? $('#items').val().split(',').map(s => s.trim()) : [],
      address: $('#address').val()
    };

    // show immediate feedback while sending
    $('#orderResult').text('Sending...');

    // send order to backend API
    $.ajax({
      url: '/api/orders',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(payload)
    }).done(function(resp) {
      // success: show success message, reset form and reload events
      $('#orderResult').html('<div class="alert alert-success">Order sent</div>');
      $('#orderForm')[0].reset();
      loadEvents();
      // hide the success message after 3 seconds with a short fade
      setTimeout(function() {
        $('#orderResult').fadeOut(300, function() {
          $(this).empty().show(); // clear content and restore container for future messages
        });
      }, 3000);
    }).fail(function(xhr) {
      // error: try to parse backend message, otherwise show generic error
      let msg = 'Error';
      try { msg = xhr.responseJSON?.error || JSON.stringify(xhr.responseJSON); } catch (e) {}
      $('#orderResult').html('<div class="alert alert-danger">' + msg + '</div>');
    });
  });

  // initial load: populate events table
  loadEvents();
  // polling: refresh events every 5 seconds so UI stays reasonably fresh
  setInterval(loadEvents, 5000);
});
