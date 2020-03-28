/*
 * Copyright (C) 2020 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testing

import (
	"github.com/syndesisio/syndesis/install/operator/pkg/syndesis/clienttools"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	discoveryfake "k8s.io/client-go/discovery/fake"
	clientset "k8s.io/client-go/kubernetes"
	gofake "k8s.io/client-go/kubernetes/fake"
	rtfake "sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func openshiftApiClient() clientset.Interface {
	api := gofake.NewSimpleClientset()
	fd := api.Discovery().(*discoveryfake.FakeDiscovery)

	res := metav1.APIResourceList{
		GroupVersion: "apps.openshift.io/4.0",
	}

	fd.Resources = []*metav1.APIResourceList{&res}

	return api
}

func FakeClientTools() *clienttools.ClientTools {
	clientTools := &clienttools.ClientTools{}
	clientTools.SetRuntimeClient(rtfake.NewFakeClient())
	clientTools.SetApiClient(openshiftApiClient())
	return clientTools
}
