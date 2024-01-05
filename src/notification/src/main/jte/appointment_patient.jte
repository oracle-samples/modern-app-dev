@param String providerName
@param String patientName
@param String date
@param String slot
@param String status

<!DOCTYPE html>
<html lang="en">
<body>
<p>
    Hello <span>${patientName}</span>,
</p>
<p>
    Here is your appointment detail with doctor <span>${providerName}</span>!
</p>
<table>
    <tbody>
    <tr>
        <td><b>Date</b></td>
        <td>${date}</td>
    </tr>
    <tr>
        <td><b>Slot</b></td>
        <td>${slot}</td>
    </tr>
    <tr>
        <td><b>Status</b></td>
        <td>${status}</td>
    </tr>
    </tbody>
</table>
<p>
    Regards, UHO
</p>
<p>
    Note: This is a system generated Email. Please do not reply to this Email.
</p>
</body>
</html>
