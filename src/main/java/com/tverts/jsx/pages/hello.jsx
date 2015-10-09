/*===============================================================+
 |            Sample Servlet implemented in JavaScript           |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

/**
 * Global variables are:
 *
 *  request
 *    Java Servlet HTTP Request;
 *
 *  response
 *    Java Servlet HTTP Response;
 *
 *  params
 *    map with all parameters of the request,
 *    both from the URL, or from URL-encoded
 *    body of POST content. Each parameter is
 *    a string, or array of strings;
 *
 *  stream
 *    Java Input Stream for requests not
 *    application/x-www-form-urlencoded,
 *    or text/*, or application/javascript,
 *    or application/json.
 *
 *    Warning: stream is defined only when
 *    Content-Type is!
 *
 *  contentType
 *    string with the content type of the request
 *    having all the parameters (such as the charset)
 *    removed from it.
 */
function get()
{

}