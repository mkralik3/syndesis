import { WithConnection } from '@syndesis/api';
import { Action, ConnectionOverview, Integration } from '@syndesis/models';
import {
  ContentWithSidebarLayout,
  IntegrationFlowStepGeneric,
  IntegrationFlowStepWithOverview,
  IntegrationVerticalFlow,
  Loader,
} from '@syndesis/ui';
import { WithLoader, WithRouteData } from '@syndesis/utils';
import * as H from 'history';
import * as React from 'react';
import { Link } from 'react-router-dom';
import { WithClosedNavigation } from '../../../containers';
import { IntegrationEditorChooseAction } from '../components';
import resolvers from '../resolvers';

function getActionHref(
  startConnection: ConnectionOverview,
  startAction: Action,
  finishConnection: ConnectionOverview,
  integration: Integration,
  action: Action
): H.LocationDescriptor {
  return resolvers.create.finish.configureAction({
    actionId: action.id!,
    finishConnection,
    integration,
    startAction,
    startConnection,
  });
}

export interface IntegrationCreatorFinishActionRouteParams {
  connectionId: string;
}

export interface IntegrationCreatorFinishActionRouteState {
  startConnection: ConnectionOverview;
  startAction: Action;
  integration: Integration;
  finishConnection: ConnectionOverview;
}

export class IntegrationCreatorFinishActionPage extends React.Component {
  public render() {
    return (
      <WithClosedNavigation>
        <WithRouteData<
          IntegrationCreatorFinishActionRouteParams,
          IntegrationCreatorFinishActionRouteState
        >>
          {(
            { connectionId },
            { startConnection, startAction, integration, finishConnection }
          ) => (
            <WithConnection id={connectionId} initialValue={finishConnection}>
              {({ data, hasData, error }) => (
                <ContentWithSidebarLayout
                  sidebar={
                    <IntegrationVerticalFlow disabled={true}>
                      {({ expanded }) => (
                        <>
                          <IntegrationFlowStepWithOverview
                            icon={
                              <img
                                src={startConnection.icon}
                                width={24}
                                height={24}
                              />
                            }
                            i18nTitle={`1. ${startAction.name}`}
                            i18nTooltip={`1. ${startAction.name}`}
                            active={false}
                            showDetails={expanded}
                            name={startConnection.connector!.name}
                            action={startAction.name}
                            dataType={'TODO'}
                          />
                          <IntegrationFlowStepGeneric
                            icon={
                              hasData ? (
                                <img src={data.icon} width={24} height={24} />
                              ) : (
                                <Loader />
                              )
                            }
                            i18nTitle={
                              hasData
                                ? `2. ${data.connector!.name}`
                                : '2. Finish'
                            }
                            i18nTooltip={hasData ? `2. ${data.name}` : 'Finish'}
                            active={true}
                            showDetails={expanded}
                            description={'Choose an action'}
                          />
                        </>
                      )}
                    </IntegrationVerticalFlow>
                  }
                  content={
                    <WithLoader
                      error={error}
                      loading={!hasData}
                      loaderChildren={<Loader />}
                      errorChildren={<div>TODO</div>}
                    >
                      {() => (
                        <IntegrationEditorChooseAction
                          breadcrumb={[
                            <Link to={resolvers.list()} key={1}>
                              Integrations
                            </Link>,
                            <Link
                              to={resolvers.create.start.selectConnection()}
                              key={2}
                            >
                              New integration
                            </Link>,
                            <Link
                              to={resolvers.create.start.selectAction({
                                connection: startConnection,
                              })}
                              key={3}
                            >
                              Start connection
                            </Link>,
                            <Link
                              to={resolvers.create.start.configureAction({
                                actionId: startAction.id!,
                                connection: startConnection,
                              })}
                              key={4}
                            >
                              Configure action
                            </Link>,
                            <Link
                              to={resolvers.create.finish.selectConnection({
                                integration,
                                startAction,
                                startConnection,
                              })}
                              key={5}
                            >
                              Finish Connection
                            </Link>,
                            <span key={6}>Choose Action</span>,
                          ]}
                          actions={data.actionsWithTo.sort((a, b) =>
                            a.name.localeCompare(b.name)
                          )}
                          getActionHref={getActionHref.bind(
                            null,
                            startConnection,
                            startAction,
                            finishConnection,
                            integration
                          )}
                        />
                      )}
                    </WithLoader>
                  }
                />
              )}
            </WithConnection>
          )}
        </WithRouteData>
      </WithClosedNavigation>
    );
  }
}
