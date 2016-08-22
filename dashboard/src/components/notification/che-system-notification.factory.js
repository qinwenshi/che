/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';


/**
 * This class is handling all application notifications.
 *
 * @author Ann Shumilova
 */
export class ApplicationNotifications {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor ($http) {
    this.$http = $http;

    this.notifications = [];

    let notification = {};
    notification.title = 'Low RAM';
    notification.content = 'The system resource are bla bla bla. Workspace';
    notification.type = 'error';

    this.notifications.push(notification);

    notification = {};
    notification.title = 'A lot users';
    notification.content = 'You have a lot users ';
    notification.type = 'info';
    this.notifications.push(notification);

    notification = {};
    notification.title = 'Its warning';
    notification.content = 'Bec carefull';
    notification.type = 'info';
    notification.icon = 'fa-plus';
    this.notifications.push(notification);

    notification = {};
    notification.title = 'Its warning';
    notification.content = 'Be carefull';
    notification.type = 'warning';
    this.notifications.push(notification);

  }

  getNotifications() {
    return this.notifications;
  }

  addNotification(notification) {
    this.notifications.push(notification);
  }

  addErrorNotification(title, content) {

  }

  addWarningNotification(title, content) {

  }

  addInfoNotification(title, content) {

  }
}
