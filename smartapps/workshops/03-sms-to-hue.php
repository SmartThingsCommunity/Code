<?php
    $url = '<YOUR SMARTAPP ENDPOINT>';

    $body = $_REQUEST['Body'];
    $data = "{'value':'$body'}";

    $options = array(
        'http' => array(
            'header'  => "Authorization: Bearer <YOUR SMARTAPP TOKEN>\nContent-type: application/json",
            'method'  => 'PUT',
            'content' => $data,
        ),
    );

    $context  = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
?>

<Response>
    <Message>This message is sent back to the user via SMS</Message>
</Response>
