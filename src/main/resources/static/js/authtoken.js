$(document).ready(function () {
    $.authenticatedAjax = function (options) {
        const token = localStorage.getItem('access_token');
        if (!token) {
            window.location.href = '/login';
            return;
        }

        options = options || {};
        options.headers = options.headers || {};
        options.headers['Authorization'] = 'Bearer ' + token;

        return $.ajax(options).fail(function (xhr) {
            if (xhr.status === 401) {
                alert('Session expired or unauthorized. Please log in again.');
                localStorage.removeItem('access_token');
                window.location.href = '/';
            }
        });
    };
});

// document.addEventListener('DOMContentLoaded', function() {
//     window.authenticatedAjax = function(options) {
//         const token = localStorage.getItem('access_token');
//         if (!token) {
//             window.location.href = 'login.html';
//             return;
//         }
//
//         options = options || {};
//         options.headers = options.headers || {};
//         options.headers['Authorization'] = 'Bearer ' + token;
//
//         const xhr = new XMLHttpRequest();
//         xhr.open(options.method || 'GET', options.url);
//
//         for (const key in options.headers) {
//             if (options.headers.hasOwnProperty(key)) {
//                 xhr.setRequestHeader(key, options.headers[key]);
//             }
//         }
//
//         xhr.onload = function() {
//             if (xhr.status >= 200 && xhr.status < 300) {
//                 if (options.success) {
//                     options.success(xhr.responseText);
//                 }
//             } else {
//                 if (xhr.status === 401) {
//                     alert('Session expired or unauthorized. Please log in again.');
//                     localStorage.removeItem('access_token');
//                     window.location.href = 'login.html';
//                 }
//                 if (options.fail) {
//                     options.fail(xhr);
//                 }
//             }
//         };
//
//         xhr.onerror = function() {
//             if (options.fail) {
//                 options.fail(xhr);
//             }
//         };
//
//         xhr.send(options.data || null);
//     };
// });
